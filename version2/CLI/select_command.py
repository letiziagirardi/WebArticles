import sys
import json
from functions import insert_user, search_user, search_all_users, delete_user, search_article_by_ean, search_article_by_codart, search_article_by_description, insert_article, modify_article, remove_article, insert_modify_price, search_price_by_codart, search_all_promo, search_promo_by_id, search_available_promo, insert_promo, remove_promo


def select_command(roles, command_mapping):
    if any(role in roles for role in command_mapping.keys()):
        print("Select command:")
        for number, description in command_mapping.items():
            print(f"{number}. {description}")
        print("exit. Exit")

        while True:
            selected_command = input("Enter your choice: ")
            if selected_command in command_mapping or selected_command == "exit":
                return selected_command
            else:
                print("Invalid choice. Please enter a valid number or 'exit'.")
    else:
        print("You do not have permission to access these commands.")
        return None

def run_operations(roles, command_mapping):
    while True:
        selected_command = select_command(roles, command_mapping)
        if selected_command == "exit":
            break
        elif selected_command in command_mapping:
            function_name = command_mapping[selected_command]
            globals()[function_name]()

# Admin commands
admin_command_mapping = {
    "1": "insert_user",
    "2": "search_user",
    "3": "search_all_users",
    "4": "delete_user",
    "5": "insert_article",
    "6": "modify_article",
    "7": "remove_article",
    "8": "insert_modify_price",
    "9": "insert_promo",
    "10": "remove_promo"
}

# User commands
user_command_mapping = {
    "1": "search_user",
    "2": "search_all_users",
    "3": "search_article_by_ean",
    "4": "search_article_by_codart",
    "5": "search_article_by_description",
    "6": "search_price_by_codart",
    "7": "search_all_promo",
    "8": "search_promo_by_id",
    "9": "search_available_promo"
}

# Admin-User commands
admin_user_command_mapping = {
    "1": "insert_user",
    "2": "search_user",
    "3": "search_all_users",
    "4": "delete_user",
    "5": "search_article_by_ean",
    "6": "search_article_by_codart",
    "7": "search_article_by_description",
    "8": "insert_article",
    "9": "modify_article",
    "10": "remove_article",
    "11": "search_price_by_codart",
    "12": "insert_modify_price",
    "13": "search_all_promo",
    "14": "search_promo_by_id",
    "15": "search_available_promo",
    "16": "insert_promo",
    "17": "remove_promo"
}


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python file.py <roles>")
        sys.exit(1)

    ruoli_string = sys.argv[1]
    ruoli = json.loads(ruoli_string)

    if ruoli == '1':
        run_operations(ruoli, admin_command_mapping)
    elif ruoli == '2':
        run_operations(ruoli, user_command_mapping)
    else:
        run_operations(ruoli, admin_user_command_mapping)
