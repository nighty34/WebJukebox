package ch.bzz.webjukebox;

import ch.bzz.webjukebox.utils.Configuration;
import ch.bzz.webjukebox.utils.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;


@SpringBootApplication
public class  WebjukeboxApplication implements WebMvcConfigurer {

	private String path = (new File(Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getParentFile().getParentFile().toString() + File.separator + "music" + File.separator).replace("file:", "");

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry){
		registry
				.addResourceHandler("/resources/**")
				.addResourceLocations("file://" + path)
				.setCachePeriod(60);
	}

	public static void main(String[] args) {
		SpringApplication.run(WebjukeboxApplication.class, args);
		Configuration.init();
		Database.init();
	}

}
