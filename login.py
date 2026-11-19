"""Rejestruje zielinskiego, loguje użytkowników i zapisuje ich tokeny dla VS Code (REST Client)."""
import json
import re
from pathlib import Path

import requests

IDP = "http://localhost:9000"
API = "http://localhost:9080/pzw/api"
REDIRECT = "http://127.0.0.1:1234/callback"

USERS = {"admin": "admin", "kowalski": "kowalski", "nowak": "nowak", "zielinski": "tajne123"}


def register():
    response = requests.put(f"{API}/users/33333333-4444-4555-8666-777777777777",
                            json={"login": "zielinski", "password": "tajne123", "birthday": "1990-01-01"})
    print("Rejestracja zielinski:", response.status_code)


def login(user, password):
    browser = requests.Session()
    # 1-2. żądanie autoryzacji, dostawca pokazuje formularz logowania
    form = browser.get(f"{IDP}/oauth2/authorize", params={
        "response_type": "code", "client_id": "pzw", "scope": "openid", "redirect_uri": REDIRECT})
    csrf = re.search(r'name="_csrf"[^>]*value="([^"]+)"', form.text).group(1)
    # 3-4. wysłanie loginu i hasła, dostawca odsyła kod w przekierowaniu
    response = browser.post(f"{IDP}/login", data={"username": user, "password": password, "_csrf": csrf},
                            allow_redirects=False)
    response = browser.get(response.headers["Location"], allow_redirects=False)
    code = re.search(r"code=([^&]+)", response.headers["Location"]).group(1)
    # 5. wymiana kodu na token
    response = requests.post(f"{IDP}/oauth2/token", auth=("pzw", "secret"),
                             data={"grant_type": "authorization_code", "code": code, "redirect_uri": REDIRECT})
    return response.json()["access_token"]


def save(tokens):
    file = Path(__file__).parent / ".vscode" / "settings.json"
    file.parent.mkdir(exist_ok=True)
    file.write_text(json.dumps({"rest-client.environmentVariables": {"$shared": tokens}}, indent=2))
    print("Tokeny zapisane w", file)


register()
tokens = {}
for user, password in USERS.items():
    tokens[f"{user}Token"] = login(user, password)
    print(f"{user}: zalogowany")
save(tokens)
