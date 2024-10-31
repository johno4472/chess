T. "implement seven HTTP endpoints"
Q. What does an HTTP endpoint represent/do?
A. Chess clients use them to communicate with the server
U. Not Yet

Q. What are my seven HTTP endpoints?
A. Login, Register, JoinGame, ListGames, Clear, Creategame, Logout
U. Yes

T. "creating your server, service, and data access classes" 
Q. What is the difference between server, service, and data access classes?
A. 
U. Not Yet

T. "You will also wright unit tests for your service classes" 
Q. How do I write a unit test?
A.
U. Not Yet

Q. Why do I only need to write a unit test for my service class?
A. 
U. Not Yet

"Required HTTP Endpoints"

T. "An endpoint is a URL that your server exposes so clients can make [HTTP] requests to your 
server" 
Q. How do I expose that part of the URL?
A.
U. Not yet

Q. What does HTTP stand for?
A. Hypertext Transfer Protocol
U. yes

Q. What does Hypertext Transfer Protocol Mean?
A.
U. Not Yet

T. "This data can be stored in the HTTP Headers, in the URL, and/or in the request body"
Q. What does that look like?
A.
U. Not yet

T. "For your server, you will use JSON strings to encode the objects we include in Request and Response bodies"
Q. Why do I need to encode in JSON?
A.
U. Not yet

Q. How do I encode in JSON?
A.
U. Not yet

C. I need to make seven HTTP endpoints that allow the user to communicate with the server
and do things. They will call an endpoint to do something specific, then the server will
request some information from them, then the server will do what they ask and respond with
some thing that they requested (like with a list of games if they asked to list the games)

"Authentication Tokens"


T. "randomized string of characters taht uniquely represent that a user has been authenticated 
with their username and password" 
Q. How do I create this random string for the AuthToken? 
A. "One easy way to create an authToken is to use the JDK UUID.randomUUID() method. For example:
import java.util.UUID;
//...
public static String generateToken() {
	return UUID.randomUUID().toString();
} 
U. Yes

T. "list games endpoint provides an authToken in the HTTP authorization header"
Q. What is the HTTP authorization header?
A.
U. Not yet

T. "That token is stored in an AuthData model object that associates the token with a username 
for future verification"
Q. What is a model object?
A.
U. Not Yet

Q. How do I store this? I am behind, so I might want to use a database, but until then?
A.
U. Not Yet

Q. So do I create a new AuthToken every time a user logs in?
A.
U. Not Yet

C. I need to create an AuthToken every time someone registers or logs in. I create this authoken
if their username and password match, then store it. I need to double check that the AuthToken
is valid in every HTTP request for carrying out the request.

"Endpoint specifications"

T: "Your server must accept the URL, HTTP Method, Header, and body that the endpoint defines."
Q. What are all of those things?
A.
U. Not Yet

Q. How does my server receive and accept those things?
A.
U. Not Yet

T. "URL Path /db \n HEEP Method DELETE"
Q. Is that "/db" what the user inputs in order to clear everything?, or is it DELETE?
A.
U. Not Yet

T. "Success Response [200] {}"
Q. Where do the success and failure responses get sent to? How do I respond to them?
A.
U. Not yet

T. "HTTP Method POST"
Q. What is the difference between the DELETE, GET, PUT, and POST HTTP methods? Are there more?
A. It looks like the POST method is used when I am doing something that alters stuff on the server.
For example when I register, I give the server new information, so I post. When it is GET, it looks
like I am just requesting information from the server but changing nothing (List Games). My guess
is that DELETE is when I'm removing something from the server (authToken or userData), and PUT
method is maybe when I am getting info and changing it, combining GET and POST (Join Game)
U. Not yet

C. For each HTTP Endpoint, I need a few things
1. To understand a DESCRIPTION of what the purpose of that endpoint is
2. The URL path, which I think the client uses to make a request
3. The HTTP METHOD, which I'm guessing tells my server what kind of action is being done
4. The HEADERS, which are just the authToken for everyting except Clear, Login, and Register
(that's because they don't need an authToken). I'm guessing the Header is something that is
required in order to carry out the request
5. The BODY, which looks like what the client has to input to fulfill the HTTP request
6. The SUCCESS RESPONSE, which is what I will return to somewhere (maybe the client) if the 
request is successful
7. The FAILURE RESPONSE, which is what I will return to maybe the Client if the request fails
(like if I don't have a valid authToken, or I enter an incorrect name to join a game)

"Required Classes"

"Data Model Classes"

T. "As part of this phase, you need to create record classes and add them"
Q. What is a record?
A. I think it's some sort of class that just initializes something
U. Not Yet

Q. Why do I need to store my records in the shared module?
A. 
U. Not Yet

C. I need to create record classes which will initialize data objects for the following:
1. UserData (made up of username, password, and email)
2. GameDate (containing gameID, whiteUsername, blackUsername, gameName, game)
3. AuthDate (comprised of the authToken and username)

"Data Access Classes"

T. "Classes that represent the access to your database are often called Data Access Objects
(DAOs)"
Q. So a data access object is a class that contains methods to access data I have stored?
A. Data access classes are responsible for storing and retrieveing server's data (users, games, etc.)
U. Yes

T. "Create your data access classes in the server/src/main/java/dataaccess package"
Q. Why is it a package and not a folder?
A.
U.

T. "This method [to create a new UserData object in the data store] may look like this:
void insertUser(UserData u) throws DataAccessException"
Q. What does this mean?
A. It looks like the method takes in an object of type UserData then stores it in server's data
U. Not Yet

C. I will need to create some Data Access Objects, classes which contain methods to Create, Read, 
Update, and Delte objects from the data store. A lot of these methods will have as parameters and 
return values the model objects like UserData I created with records

"DataAccessException"

C. Throw DataAccessException, which came with starter code, if Data Access method calls fail, like
trying update a game that doesn't exist.

"Example Data Access Methods"

C. My DAOs should be able to [clear] all data, [createUser], [getUser], [createGame], [getGame]
 with given gameID, [listGames], [updateGame] by changing gameID any time a player joins or a 
move is made, [createAuth], [getAuth] retrieving an authorization, and [deleteAuth] to make it
no longer valid.

"Data Access Interface"

N: I will store (in this phase) data in maps, sets, or lists). I will use SQL next time

T: "...using an interface ... creates a flexible architecture that allows you to change how
things work without rewriting all of your code."
Q. What is a Java Interface?
A.
U. Not Yet

Q. How do I create a Java Interface?
A. 
U. Not Yet

N: place your data access classes in a folder named server/src/main/java/dataaccess

"Service Classes"

T. "Service classes implement the actual functionality of the server... logics associated with 
the web endpoints"

T. "Have a separate service class for each group of related endpoints"

T. "Each service method receives a Request object containing all the information it needs to do its
work"

T. "Returns corresponding result object containign the output of the method"
Q. Is the output either the success or failure response?
A.
U. Not Yet

T. "To do their work, service classes need to make heavy use of the Model classes and Data Access
classes described above.

N. service classes in folder named server/src/main/java/service

"Request and Result Classes"

N. Service class methods receive request objects as input and return result objects as output

T. "The contents of these classes can be derived from the JSON inputs and outputs of the web 
endpoints documented above.
Q. How do they receive the JSON object?
A.
U. Not Yet

T. "you could use the model UserData object that you will also use when you call your data access
layer"
Q. What do they mean by "Data Access layer"?
A.
U. Not Yet

N. Looks like I should use the same model objects for dataAccess as for requests and results

N. Looks based on the JSON input or output, I need to derive which model or output that relates to

N. Looks like I even need record classes for things like LoginResult
Q. Do I need to have a separate file for each record?
A.
U. Not Yet

Q. How do I know when to make a record or how many?
A.
U. Not Yet

"Serialization"

N. Field names in Request and Result classes must match exactly the property names in the JSON strings,
including capitalization
Q. How do I make a Request and Result class?
A.
U.


N. Serialize like this:
var serializer = new Gson();
var game = new ChessGame();
//serialize to JSON
var json = serializer.toJson(game);
//deserialize back to ChessGame
game = serializer.fromJson(json, ChessGame.class);

Q. In the code above, what is the datatype "var"?
A.
U. Not Yet

"Server Handler Classes"

T. "Server Handler classes serve as a translator between HTTP and Java"

T. "will convert an HTTP request into Java usable objects & data"
N. So I guess the client communicates to me through HTTP and I do everything in Java

T. "When the service responds, the handler converts the response object back to JSON and sends the
HTTP response"

N. Create as many handler classes as are necessary
Q. How do I know which handler classes to implement and how many?
A.
U. Not Yet


