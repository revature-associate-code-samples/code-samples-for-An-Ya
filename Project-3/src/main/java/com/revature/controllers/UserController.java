package com.revature.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.annotations.CognitoAuth;
import com.revature.dto.CohortUserListOutputDto;
import com.revature.dto.UserListInputDto;
import com.revature.models.Cohort;
import com.revature.models.User;
import com.revature.services.CohortService;
import com.revature.services.UserService;
import com.revature.utils.CognitoUtil;
import com.revature.utils.ResponseEntityUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CohortService cohortService;

	@Autowired
	private ResponseEntityUtil responseEntity;

	Logger log = Logger.getRootLogger();

	@GetMapping()
	public ResponseEntity<List<User>> findAll() {

		return new ResponseEntity<List<User>>(userService.findAll(), HttpStatus.OK);
	}

	// need to change this to unique end point
	@GetMapping("id/{id}")
//	@Logging()
	// Might need to change?
	public ResponseEntity<User> findOneById(@PathVariable int id) {

		return new ResponseEntity<User>(userService.findOneById(id), HttpStatus.OK);
	}

	@GetMapping(path="email/{email:.+}")
	public ResponseEntity<User> findOneByEmail(@PathVariable String email) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			return new ResponseEntity<User>(userService.findOneByEmail(java.net.URLDecoder.decode(email.toLowerCase(), "utf-8")),headers, HttpStatus.OK);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Need to fix
	@GetMapping("info")
	public ResponseEntity<User> userInfo() {

		return new ResponseEntity<User>(userService.userInfo(), HttpStatus.OK);
	}

	@GetMapping("cohorts/{id}")
//	@Logging()
	public ResponseEntity<List<User>> findAllByCohortId(@PathVariable int id) {
		return new ResponseEntity<List<User>>(userService.findAllByCohortId(id), HttpStatus.OK);
	}

	@PostMapping()
	@CognitoAuth(role = "user")
	public ResponseEntity<User> saveUser(@RequestBody User u) throws IOException, URISyntaxException {
		User user = cognitoUtil.registerUser(u);
		if (user == null) {
//			User tempUser = userService.findOneByEmail(u.getEmail());
			return new ResponseEntity<User>(userService.findOneByEmail(u.getEmail()), HttpStatus.CONFLICT);
		}else {
			return new ResponseEntity<User>(user, HttpStatus.OK);	
		}

	}

	@PatchMapping("{userid}/cohorts/{cohortid}")
	public ResponseEntity<User> updateCohort(@PathVariable int userid, @PathVariable int cohortid) {
		Cohort cohort = cohortService.findOneByCohortId(cohortid);
		User user = userService.findOneById(userid);
		user.getCohorts().add(cohort);

		return new ResponseEntity<User>(userService.saveUser(user), HttpStatus.OK);
	}

	@PatchMapping()
	public ResponseEntity<User> updateProfile(@RequestBody User u) {
		return new ResponseEntity<User>(userService.updateProfile(u), HttpStatus.OK);
	}

}
