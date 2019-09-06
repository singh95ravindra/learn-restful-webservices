# learn-restful-webservices
this project is to learn restful web-services with spring boot 

#Step-1: create a rest controller 'HelloWorldController' like this

@RestController
public class HelloWorldContoller {

	@RequestMapping(method = RequestMethod.GET, path = "/hello-world")
	public String helloWorld() {
		return "Hello World";
	}
}

this method just return a 'Hello world' string to the browser when accessed at path - '/hello-world'

#Step-2: 'HelloWorldController'' is enhanced to return a bean

@RestController
public class HelloWorldContoller {

	@RequestMapping(method = RequestMethod.GET, path = "/hello-world")
	public String helloWorld() {
		return "Hello World";
	}

	@GetMapping("/hello-world-bean")
	public HelloWorldBean helloWorldBean() {
		return new HelloWorldBean("Hello Ravinder");
	}
}

also annotation of mapping is also updated to @GetMapping, there are mapping annotations are available corresponding to Http methods.

#Step-3: Enhance 'HelloWorldController' to accept path-variable

below method is added to do so

	@GetMapping("/hello-world-bean/path-variable/{name}")
	public HelloWorldBean helloWorldBean(@PathVariable String name) {
		return new HelloWorldBean("Hello " + name);
	}
	
#Step-4: created an entity User and also created a UserResource with methods to insert and retrieve users from embedded H2 database by using JpaRepository

@Entity
public class User {
	@Id
	@GeneratedValue
	private long id;
	private final String name;
	private final Date birthDate;

	public User(String name, Date birthDate) {
		super();
		this.name = name;
		this.birthDate = birthDate;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", birthDate=" + birthDate + "]";
	}

}

and UserResource

@RestController
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/users")
	public List<User> retrieveAllUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/users/{id}")
	public Resource<User> retrieveById(@PathVariable long id) {
		final Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			throw new UserNotFoundException("Id-" + id);
		}
		final Resource<User> resource = new Resource<User>(user.get());
		final ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		resource.add(linkTo.withRel("all-users"));
		return resource;
	}

	@PostMapping("/users")
	public ResponseEntity<User> insert(@Valid @RequestBody User user) {

		final User savedUser = userRepository.save(user);

		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedUser.getId()).toUri();

		return ResponseEntity.created(location).build();
	}
}

which makes use of 
and repository

public interface UserRepository extends JpaRepository<User, Long> {

}

also handled exception by implementing a custom exception handler

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) throws Exception {
		final ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(Exception ex, WebRequest request) throws Exception {
		final ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.NOT_FOUND);
	}
}

which throws a custom exception in the following format

public class ExceptionResponse {

	private final Date timestamp;
	private final String message;
	private final String details;

	public ExceptionResponse(Date timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

}

