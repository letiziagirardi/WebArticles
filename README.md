# SDE Final Project

Final project for Service Design and Engineering. Letizia Girardi, a.y. 2022/23.

## Running the project

In order to build the correct environment, from the root folder of the project, please run:

```
cd version2
bash shop.sh build 
```

In order to effetivelly use the project, from the current folder, please run:

```
cd CLI
python webArticle.py <command>
```

This command provides the CLI of the service. To learn more about the available commands and options, run `python webArticle.py --help`.


## Original Proposal
The project WebArticles is a comprehensive initiative designed to offer an advanced service for efficiently managing products. This includes the integration of independent services to facilitate the acquisition of promotional offers and pricing information. The service will offer valuable insights into available articles, associated promotions, and the most favorable pricing options.

The service employs a microservices architecture, consisting in the operation of indipendent microservises whhich operate to fulfill specific roles:
* **Articles Service**: Manages articles and interacts with the price and promo services to provide article information and related price.
* **Promo Service**: This service handles promotional activities, providing active promos.
* **Price Service**: Deals with price-related information. Receives requests from the articles service. 
* **Gestuser Service**: This service is responsible for user authentication and authorization, providing access control to other services. Moreover, it is possible to create and managing users' profiles.

Finally, In order to simplify client interaction, handle authentication and authorization across all microservices and distribute incoming requests across multiple instances of services, the API Gateway service has been implemented.


## The Workflow
A user of the WebArticle service can perform the following workflow:
1. **Sign up for an account:** Create a new account by providing the necessary information.
2. **Log in to the platform:** Use your credentials to log in to the platform.
3. **Perform Actions Based on User Role:**
   - Admin Role:
     - Execute a range of associated actions, including:
       - Inserting a new user.
       - Searching for a specific user.
       - Retrieving information about all users.
       - Deleting a user.
       - Finding an article using its Ean.
       - Locating an article through its CodArt.
       - Searching for an article based on its description.
       - Adding a new article.
       - Modifying an existing article.
       - Removing an article.
       - Searching for the price associated with a specific CodArt.
       - Inserting a price if not already present, or modifying it if it exists.
       - Exploring all available promotions.
       - Searching for a promotion by its unique ID.
       - Discovering promotions available for a particular article.
       - Adding a new promotion.
       - Removing a promotion.

  ## Documentation 
Each service offers a dedicated API documentation. To access this documentation, simply navigate to the /swagger-ui page of the respective service. For instance, to explore the API documentation for articles service, visit http://localhost:8080/articles/swagger-ui. 
