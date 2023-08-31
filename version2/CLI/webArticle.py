import argparse
import json
import requests
import base64
import bcrypt
import subprocess

def check_user_existence(username, password):
    url = f"http://localhost:8080/gestuser/utenti/cerca/userid/{username}"
    headers = {"Authorization": f"Basic UmVhZFVzZXI6QmltQnVtQmFtXzIwMTg="}

    response = requests.get(url, headers=headers)

    if response.status_code == 200:
        if len(response.content) == 0:
            return False
        else:
            data = response.json()
            hashed_password_from_server = data.get("password")

            if bcrypt.checkpw(password.encode(), hashed_password_from_server.encode()):
                print("Password is correct.")
                return data.get("ruoli")
            else:
                print("Password is incorrect.")
                return False
    else:
        print("Error:", response.status_code)
        return False

def signup():
    """Create a new user."""
    username = input("Choose a username: ")
    password = input("Choose a password: ")
    password_again = input("Enter the password again: ")

    if password == password_again:
        while True:
            role_choice = input("Enter role of the user : \n1. ADMIN \n2. USER \n3. ADMIN AND USER: ")
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

        response = requests.post(url, headers=headers, json=data)

        if response.status_code == 201:
            print("New user: " , data)
            token = base64.b64encode(f"{username}:{password}".encode()).decode()
            config = {
                "username": username,
                "ruoli": role_choice,
                "token": token
            }
            with open('config.json', 'w') as config_file:
                json.dump(config, config_file)

            authenticated = True
            print(f"✔ User {username} created")
        else:
            print("Error:", response.status_code)
    else:
        print("Passwords do not match")


def login():
    """Log in with username and password."""
    username = input("Enter username: ")
    password = input("Enter password: ")

    ruoli = check_user_existence(username, password)
    if ruoli:
        print("User exists.")

        token = base64.b64encode(f"{username}:{password}".encode()).decode()

         # Map roles to numerical values
        if ruoli == ['ADMIN'] :
            roles = '1'
        elif ruoli == ['USER']:
            roles = '2'
        else:
            roles = '3'

        # Update configuration file
        config = {
            "username": username,
            "ruoli": roles,
            "token": token
        }
        with open('config.json', 'w') as config_file:
            json.dump(config, config_file)

        authenticated = True
        print("✔ Logged in")
    else:
        print("Login failed")

def execute_select_command():
    """Read configuration and execute select_command.py"""
    with open('config.json', 'r') as config_file:
        config = json.load(config_file)

    ruoli = config.get("ruoli", [])
    ruoli_string = json.dumps(ruoli)

    select_command = ["python", "select_command.py", ruoli_string]
    subprocess.call(select_command)


def main():
    parser = argparse.ArgumentParser(description="WebArticle - Command Line Interface")
    subparsers = parser.add_subparsers(dest="command")

    signup_parser = subparsers.add_parser("signup", help="Create a new user")
    signup_parser.set_defaults(func=signup)

    login_parser = subparsers.add_parser("login", help="Log in with username and password")
    login_parser.set_defaults(func=login)

    select_parser = subparsers.add_parser("commands", help="Select and execute available commands based on your role")
    select_parser.set_defaults(func=execute_select_command)

    args = parser.parse_args()
    if hasattr(args, "func"):
        args.func()

if __name__ == "__main__":
    main()
