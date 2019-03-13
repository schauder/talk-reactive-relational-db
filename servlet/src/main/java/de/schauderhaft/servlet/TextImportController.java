/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Jens Schauder
 */
@RestController
public class TextImportController {

	@Autowired
	RowRepository rows;

	@RequestMapping("/")
	public String index() {
		return "Greetings from reactive Spring Boot!";
	}

	// test with
	// curl -X POST -H "Content-Type: text/plain" --data-binary @alice.txt http://localhost:9090/upload
	@PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String upload(@RequestBody String content) {

		return StreamSupport.stream(
				rows.saveAll(
						Arrays.stream(
						content.split("\n"))
								.map(l -> new Row(null, l))
								.collect(Collectors.toList())).spliterator(), false
		)
				.map(Row::getId)
				.collect(Collectors.toList())
				.toString();
	}

	// test with
	// curl http://localhost:8080/all
	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
	public String all() {

		return rows.findAll().toString();
	}
}
