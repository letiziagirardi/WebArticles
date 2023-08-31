import requests
import json
import base64
import bcrypt
import datetime

def execute_request(method, url, headers, data=None):
    print(f"Executing the following request:")
    print(f"{method} {url}")
    print(f"Headers: {headers}")

    if data:
        print("Request Body:")
        print(data)

    response = requests.request(method, url, headers=headers, json=data)

    print("Response:")
    if(response.text):
        print(response.text)
    else:
        print(response.status_code)

# Function to read input
def read_input(prompt):
    return input(prompt)

# Function to read user input securely (password)
def read_secure_input(prompt):
    import getpass
    return getpass.getpass(prompt)

# Function for getting token
def authorization():
    with open('config.json', 'r') as config_file:
        config = json.load(config_file)
    token = config.get("token")
    return token

# =============================
# ===== GestUser
# =============================

def insert_user():
    print("Insert User function")

    username = input("Enter new username: ")
    password = input("Enter new password: ")

    while True:
        role_choice = input("Enter role of the user : 1. Admin - 2. User - 3. Admin and User \nSelect the correspondent number: ")
        if role_choice in ['1', '2', '3']:
            if role_choice == '1':
                roles = ['ADMIN']
            elif role_choice == '2':
                roles = ['USER']
            else:
                roles = ['ADMIN', 'USER']
            break
        else:
            print("Invalid choice. Please enter a valid number.")

    # Send the username and hashed password to your server to create a new user
    url = "http://localhost:8080/gestuser/utenti/inserisci"
    headers = {
        "Authorization": f"Basic QWRtaW46TWFnaWNhQnVsYV8yMDE4",
        "Content-Type": "application/json"
    }
    data = {
        "userId": username,
        "password": password,
        "attivo": "Si",
        "ruoli": roles
    }

    execute_request("POST", url, headers, data)

def search_user():
    print("Search User by userId")
    userId = input("Enter the userId: ")

    url = f"http://localhost:8080/gestuser/utenti/cerca/userid/{userId}"
    headers = {"Authorization": "Basic UmVhZFVzZXI6QmltQnVtQmFtXzIwMTg="}

    execute_request("GET", url, headers)

def search_all_users():
    print("Search All Users")

    url = "http://localhost:8080/gestuser/utenti/cerca/tutti"
    headers = {"Authorization": "Basic UmVhZFVzZXI6QmltQnVtQmFtXzIwMTg="}

    execute_request("GET", url, headers)


def delete_user():
    print("Remove User by userId")
    userId = input("Enter the userId: ")

    url = f"http://localhost:8080/gestuser/utenti/elimina/{userId}"
    headers = {
        "Authorization": "Basic QWRtaW46TWFnaWNhQnVsYV8yMDE4",
        "Content-Type": "application/json"
    }

    execute_request("DELETE", url, headers)


# =============================
# ===== Articles
# =============================


def search_article_by_ean():
    print("Search Article by Ean")

    ean = input("Enter the Ean: ")

    current_token = authorization()

    url = f"http://localhost:8080/articles/articoli/cerca/ean/{ean}"
    headers = {"Authorization": f"Basic {current_token}"}

    execute_request("GET", url, headers)


def search_article_by_codart():
    print("Search Article by CodArt")

    codArt = input("Enter the CodArt: ")
    current_token = authorization()

    url = f"http://localhost:8080/articles/articoli/cerca/codice/{codArt}"
    headers = {"Authorization": f"Basic {current_token}"}

    execute_request("GET", url, headers)


def search_article_by_description():
    print("Search Article by Description")

    description = input("Enter the Description: ")

    current_token = authorization()

    url = f"http://localhost:8080/articles/articoli/cerca/descrizione/{description}"
    headers = {"Authorization": f"Basic {current_token}"}

    execute_request("GET", url, headers)


def search_all_articles():
    print("Search All Articles")

    current_token = authorization()

    url = "http://localhost:8080/articles/articoli/cerca/tutti"
    headers = {"Authorization": f"Basic {current_token}"}

    execute_request("GET", url, headers)


def insert_article():
    print("Insert Article")

    print("Enter article details:")

    while True:
        # Prompt the user for codArt
        codArt = input("Enter codArt: ")

        # Check the length of codArt
        if 5 <= len(codArt) <= 20:
            break  # Exit the loop if the length is valid
        else:
            print("Error: codArt must have a number of characters between 5 and 20")

    descrizione = input("descrizione: ")
    um = input("um (PZ/KG): ")
    pzCart = int(input("pzCart: "))
    idStatoArt = 1
    pesoNetto = float(input("pesoNetto: "))
    famAssortId = 1

    current_date = datetime.datetime.now().strftime("%Y-%m-%d")

    json_data = {
        "codArt": codArt,
        "descrizione": descrizione,
        "um": um,
        "pzCart": pzCart,
        "idStatoArt": idStatoArt,
        "pesoNetto": pesoNetto,
        "dataCreaz": current_date,
        "famAssort": {
            "id": famAssortId
        }
    }

    current_token = authorization()

    url = "http://localhost:8080/articles/articoli/inserisci"
    headers = {
        "Authorization": f"Basic {current_token}",
        "Content-Type": "application/json"
    }

    execute_request("POST", url, headers, json_data)


def modify_article():
    print("Modify Article")
    current_token = authorization()

    # Ask the user for input
    codArt = input("Enter codArt: ")
    descrizione = input("Enter new descrizione: ")
    current_date = datetime.datetime.now().strftime("%Y-%m-%d")

    # Construct the JSON data with user input
    data = {
        "codArt": codArt,
        "descrizione": descrizione,
        "um": "PZ",
        "codStat": " TEST",
        "pzCart": 1,
        "pesoNetto": 0,
        "idStatoArt": "1",
        "dataCreaz": current_date,
        "famAssort": {
            "id": 1
        }
    }

    url = f"http://localhost:8080/articles/articoli/modifica"
    headers = {
        "Authorization": f"Basic {current_token}",
        "Content-Type": "application/json"
    }

    execute_request("PUT", url, headers, data)


def remove_article():
    print("Remove Article")

    codArt = input("Enter the CodArt: ")

    current_token = authorization()
    url = f"http://localhost:8080/articles/articoli/elimina/{codArt}"
    headers = {
        "Authorization": f"Basic {current_token}"
    }

    execute_request("DELETE", url, headers)



# =============================
# ===== Price
# =============================

def search_price_by_codart():
    print("Search Price of Article by CodArt")
    current_token = authorization()

    # Ask the user for input
    codArt = input("Enter the CodArt: ")

    url = f"http://localhost:8080/price/prezzi/cerca/codice/{codArt}"
    headers = {
        "Authorization": f"Basic {current_token}"
    }

    execute_request("GET", url, headers)


def insert_modify_price():
    print("Insert Price if not exists, modify it otherwise")
    current_token = authorization()

    # Ask the user for input
    codArt = input("Enter codArt: ")
    prezzo = float(input("Enter prezzo: "))  # Convert input to float

    url = "http://localhost:8080/price/prezzi/inserisci"
    headers = {
        "Authorization": f"Basic {current_token}",
        "Content-Type": "application/json"
    }
    data = {
        "codArt": codArt,
        "idList": 1,
        "prezzo": prezzo
    }

    execute_request("POST", url, headers, data)


# =============================
# ===== Promo
# =============================

def search_all_promo():
    print("Search All Promo")
    current_token = authorization()

    url = "http://localhost:8080/promo/promo/cerca/tutti"
    headers = {
        "Authorization": f"Basic {current_token}"
    }

    execute_request("GET", url, headers)


def search_promo_by_id():
    print("Search Promo by idPromo")
    current_token = authorization()

    # Ask the user for input
    idPromo = input("Enter the idPromo: ")

    url = f"http://localhost:8080/promo/promo/cerca/id/{idPromo}"
    headers = {
        "Authorization": f"Basic {current_token}"
    }

    execute_request("GET", url, headers)


def search_available_promo():
    print("Search Promo by CodArt")
    current_token = authorization()

    # Ask the user for input
    codArt = input("Enter the CodArt of the required Article: ")

    url = f"http://localhost:8080/promo/prezzo/promo/codice/{codArt}"
    headers = {
        "Authorization": f"Basic {current_token}"
    }

    execute_request("GET", url, headers)


def insert_promo():
    print("Add a new Promo")
    current_token = authorization()

    # Ask the user for input
    idPromo = input("Enter the idPromo: ")
    codArt = input("Enter codart: ")
    oggetto = input("Enter price: ")


    data = {
        "idPromo": idPromo,
        "anno": 2023,
        "codice": "TS01",
        "descrizione": f"PROMO TEST {codArt}",
        "dettPromo": [
            {
                "id": "-1",
                "riga": 1,
                "codart": codArt,
                "codfid": None,
                "inizio": "2023-01-01",
                "fine": "2023-12-31",
                "oggetto": oggetto,
                "isfid": "Si",
                "tipoPromo": {
                    "descrizione": "TAGLIO PREZZO",
                    "idTipoPromo": "1"
                }
            }
        ],
        "depRifPromo": [
            {
                "id": 4,
                "idDeposito": "525"
            }
        ]
    }

    url = "http://localhost:8080/promo/promo/inserisci"
    headers = {
        "Authorization": f"Basic {current_token}",
        "Content-Type": "application/json"
    }

    execute_request("POST", url, headers, data)


def remove_promo():
    print("Remove Promo by Id")
    current_token = authorization()

    # Ask the user for input
    idPromo = input("Enter the Id: ")

    url = f"http://localhost:8080/promo/promo/elimina/{idPromo}"
    headers = {
        "Authorization": f"Basic {current_token}"
    }

    execute_request("DELETE", url, headers)
