package com.practice.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.practice.rest.webservices.restfulwebservices.exception.UserNotFoundException;
import com.practice.rest.webservices.restfulwebservices.post.Post;
import com.practice.rest.webservices.restfulwebservices.post.PostRepository;

@RestController
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

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
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {

		final User savedUser = userRepository.save(user);

		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedUser.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable long id) {
		userRepository.deleteById(id);
	}

	@GetMapping("/users/{id}/posts")
	public List<Post> retrieveAllPostsForUser(@PathVariable long id) {
		final Optional<User> findById = userRepository.findById(id);
		if (!findById.isPresent()) {
			throw new UserNotFoundException("Id-" + id);
		}

		return findById.get().getPosts();
	}

	@PostMapping("/users/{id}/posts")
	public ResponseEntity<Post> createPost(@PathVariable long id, @Valid @RequestBody Post post) {
		final Optional<User> findById = userRepository.findById(id);
		if (!findById.isPresent()) {
			throw new UserNotFoundException("Id-" + id);
		}
		post.setUser(findById.get());
		postRepository.save(post);

		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(post.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}

}
