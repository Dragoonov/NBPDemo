# NBP Demo application

Project follows Clean Architecture principles.

I divided project into presentation, data and domain layers (+app).

App layer serves as a connection between other layers + dependency graph is made here.
I was wondering between splitting Hilt modules into its respective layers but according to clean architecture, domain layer should be as independent as possible.
Currently, you can place domain module into any other Kotlin app, because there are no external libs/dependencies in there (except of basic unit testing libraries).
Therefore, also Hilt library is moved outside of domain layer, which makes other layer responsible for constructing its dependencies, and I choose app to do so, as it has to have all other modules as dependencies anyway to construct a Hilt graph.
This way, despite data layer being "able" to have Hilt dependency, I choose to construct its Hilt module also in app, so all graph construction is made in one place.

Data layer is responsible for android specific operations like saving to database using Room, getting from API using Retrofit.  

Domain layer contains domain models and domain logic, so logic that should describe app's functionalities (as use cases, for example).   

Presentation layer contains android specific view logic, like ViewModels and Composables.  

## Functionality:

App downloads list of currencies from NBP api: https://api.nbp.pl/ and displayes them to user on screen.
Because of NBP intricacies, like split between table A and B of currencies, I added a caching logic responsible for saving currency code - table pairs, because on currency details screen, which user can enter after clicking on a currency, an API call for details is made, and that call requires table variable.
I didn't want to leak this API detail (existence of tables A and B) into domain layer, as it's just an API property, so I'm saving that in local storage (and in memory), so when user goes into details, a table variable is pulled from cache. Database storage is required if application process is killed while
being on details screen and all in-memory cache is wiped.

I decided to merge table A and B currencies into one list, as I don't see any differences between them, except of different content.

Next development could involve adding a paging functionality. It doesn't match with NBP api, but there could be an adapter made in data layer to portion incoming data. I decided to skip it in this implementation as:
- It's not really needed for this size of data we receive
- It can be tricky to implement with clean architecture using default approach (paging3 from android), as Paging library would have to span across all layers, polluting domain layer slightly.
- I don't want to overenginner simple app. Whole clean architecture approach is an overkill for such small app, but I wanted to show how I would approach bigger projects, and how I'd structurize application for future growth.
