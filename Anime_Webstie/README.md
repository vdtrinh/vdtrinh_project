# Otakhub
## Group E: Milestone 2

### API Endpoints

| Method | Route | Description |
|--------|-------|-------------|
| `GET` | `/users` | Returns all users registered in the app. |
| `GET` | `/users/:userId` | Returns user specified by `userId` |
| `GET` | `/users/:userId/library` | Returns the user's library |
| `GET` | `/users/:userId/library/animes/:animeId` | Returns the user's specific anime they want |
| `GET` | `/users/:userId/library/lightnovels/:lightnovelId` | Returns the user's specific lightnovel they want |
| `GET` | `/users/:userId/library/mangas/:mangaId` | Returns the user's specific manga they want |
| `GET` | `/users/:userId/history` | Returns the user's history |
| `GET` | `/users/:userId/history/animes/:animeId` | Returns the user's specific anime they want |
| `GET` | `/users/:userId/history/lightnovels/:lightnovelId` | Returns the user's specific lightnovel they want |
| `GET` | `/users/:userId/history/mangas/:mangaId` | Returns the user's specific manga they want  |
| `POST` | `/users` | Creates a new user |
| `POST` | `/users/:userId/library/animes` | Add an anime to the user's library |
| `POST` | `/users/:userId/library/lightnovels` | Add a light novel to the user's library |
| `POST` | `/users/:userId/library/mangas` | Add an manga to the user's library |
| `POST` | `/users/:userId/history/animes` | Add a anime to the user's history |
| `POST` | `/users/:userId/history/lightnovels` | Add a lightnovel to the user's history |
| `POST` | `/users/:userId/history/mangas` | Add an manga to the user's history |
| `PUT` | `/users/:userId/history/animes/:animeId` | Update the anime episode place to the user's history |
| `PUT` | `/users/:userId/history/lightnovels/:lightnovelId` | Add a lightnovel chapter place to the user's history |
| `PUT` | `/users/:userId/history/mangas/:mangaId` | Add an manga chapter place to the user's history |
| `DELETE` | `/users/:userId` | Deletes a user. | 
| `DELETE` | `/users/:userId/library` | Deletes a piece of media from the library |
| `DELETE` | `/users/:userId/history` | Deletes a peice of media from the history |
| `GET` | `/media/animes` | Returns all animes from our data |
| `GET` | `/media/lightnovels` | Returns all lightnovels from our data |
| `GET` | `/media/mangas` | Returns all mangas from our data |
| `GET` | `/media/animes/:animeId` | Returns a specific animes from our data |
| `GET` | `/media/lightnovels/:lightnovelId` | Returns a specific lightnovels from our data |
| `GET` | `/media/mangas/:mangaId` | Returns a specific mangas from our data |
| `GET` | `/media/animes/genres/:genreId` | Returns a specific of a genre animes from our data |
| `GET` | `/media/lightnovels/genres/:genreId` | Returns a specific lightnovels of a genre from our data |
| `GET` | `/media/mangas/genres/:genreId` | Returns a specific mangas of a genre from our data |

### HTML Implementation
| HTML | Description | Progression | Wireframes | 
|--------|-------|-------------| ------- | 
| Index | Page where the user will first see when going to Otakhub | Completed | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/SignInOrSignUp.PNG">W1| 
| Login | Page where the user enters their email and password to access Otakhub | Completed | Need to Create |  
| Sign-up | Page where the user enters information to gain access to Otakhub | To-do | Need to Create | 
| Discover | Page whre the user can discover media on Otakhub | Completed | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/Discover.PNG">W2 | 
| Profile | Page where the user can access their history and profile settings | In Progress | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/ProfileScreen.PNG">W3 | 
| Favorites | Page where the user can access their favorited media | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/FavScreen.PNG">W4 | 
| Manga Homepage | Page where the user can view different genres of Manga | Completed | Need to Create | 
| Anime Homepage | Page where the user can view different genres of Anime | Completed | Need to Create | 
| Light Novel Homepage | Page where the user can view different genres of Light Novel | Completed | Need to Create |
| Search | User can search for a specific media | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/SearchScreen.PNG">W5 |
| Search By Genre | Page where the user can search a media by genre | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/SearchByGenre.PNG">W6 | 
| Search Results | Page where the user can view media by the search criteria | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/SearchResult.PNG">W7 | 
| Manga Info | Page where the user can access the information of a specific Manga page | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/ProfileScreen.PNG">W8|
| Anime Info | Page where the user can access the information of a specific Anime page | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/ProfileScreen.PNG">W9 | 
| Light Novel Info | Page where the user can access the information of a specific Light Novel page | To-do | <a href="https://github.ncsu.edu/engr-csc342/csc342-2023Spring-groupE/blob/main/Proposal/ProfileScreen.PNG"> W10 |
 
### Progression

#### What is done
 * Most of our front-end pages are now dynamic except for those that uses the users information and data. Pages such as displaying anime, manga, and light novels are mostly finished except for displaying a specific type of manga, anime, and light novel which are fairly easy after one is done. 

### What is not done
 * User authentication and the frontend pages that are associated with it
 
### Team Contributions

#### Alex Cail

  * Came up with REST API endpoints as a team.
  * Developed webpage wireframes as a team.
  * Created `index.html`, `profile.html`, `login.html`, and `discover.html`, as well as corresponding CSS pages.
  * Created AnimeHomePage, MangaHomePage, LightNovelHomePage, and manga page. 
  
#### Anderson Chau
  
* We did API endpoints together as a team. 
* Created the html pages AnimeHomepage, MangaHomePage, and LightNovelHomePage. 
* Helped write the readME. 
* implemented the rest of the api calls of the code

#### Vu Trinh

* Setting up routes and server.js files to link everything together. 
* Created anime and search by genre wireframe for this iteration. 
* We did API endpoints together as a team
* worked on the user authentication 
