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