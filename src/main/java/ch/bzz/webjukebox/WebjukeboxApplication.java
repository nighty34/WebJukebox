package ch.bzz.webjukebox;

import ch.bzz.webjukebox.utils.Configuration;
import ch.bzz.webjukebox.utils.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class  WebjukeboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebjukeboxApplication.class, args);
		Configuration.init();
		Database.init();
	}

}
