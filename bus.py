from .. import loader, main, utils

import logging
import requests
import json
import telethon

from datetime import datetime

logger = logging.getLogger(__name__)


def register(cb):
    cb(BusMod())


class BusMod(loader.Module):
    """Bus reminder"""
    #strings = {"name": "BusMod"}

    def __init__(self):
        self.name = _("BusMod")

    async def client_ready(self, client, db):
        self._db = db
        self._client = client
    
    async def busurlcmd(self, message):
        args = utils.get_args_raw(message)
        return self._db.set(__name__, "url", args)

    def getUrl(self):
        return self._db.get(__name__, "url", "")

    async def setUrl(self):
        return self._db.set(__name__, "url", "")

    async def buscmd(self, message):
        """Bus CMD"""
        args = utils.get_args_raw(message)
        await message.edit("<code>Procesando...</code>")
        dbUrl = self.getUrl()
        if not args:
            stop = "tuzsa-716"
        else:
            stop = "tuzsa-" + args
        url = dbUrl + stop + ".json"

        tries = 0
        response = requests.get(url)
        logger.error(response)
        while response.status_code == 400 and tries < 10:
            response = requests.get(url)
            tries += 1
            logger.error(tries)
            await message.edit("<code>Intento #" + str(tries) + "...</code>")

        if(response.status_code == 200):
            jsonDumps = json.dumps(response.json(), sort_keys=True)
            jsonObj = json.loads(jsonDumps)

            datetime_str = jsonObj['lastUpdated']
            datetime_object = datetime.strptime(datetime_str, "%Y-%m-%dT%H:%M:%SZ")
            time = datetime_object.strftime("%H:%M:%S")

            stopName = jsonObj['title'][5:].split("Líneas", 1)[0]
            msg = stopName + "\n"
            for destino in jsonObj['destinos']:
                linea = destino['linea']
                primero = destino['primero'] if not destino['primero'].startswith('0') else "En la parada."
                segundo = destino['segundo'] if not destino['segundo'].startswith('0') else "En la parada."
                msg += "<b>• " + linea + ":</b>\n\t\t\t" + primero + "\n\t\t\t" + segundo + "\n"
            msg += "\n<i>Actualizado a la(s): " + time + "</i>"
        elif response.status_code == 404:
            msg = "<code>Esa parada no existe.</code>"
        else:
            msg = "<code>API REST Zaragoza (v2) caída (" + str(response.status_code) + ")</code>"
        await message.edit(msg)
