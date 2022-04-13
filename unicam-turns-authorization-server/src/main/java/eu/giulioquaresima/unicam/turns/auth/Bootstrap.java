package eu.giulioquaresima.unicam.turns.auth;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application's bootstrap, which can be terminated gracefully pressing ENTER, 
 * which is useful in certain environments (notably Eclipse) where when you stop 
 * a process from the console (using the red button), Eclipse "sigkills" the process
 * and so the Spring Boot's shutdown hooks won't be called.
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@SpringBootApplication (scanBasePackageClasses = Bootstrap.class)
//@ServletComponentScan (basePackageClasses = FuckFilter.class)
public class Bootstrap
{
	public static void main(String[] args)
	{
		ConfigurableApplicationContext app = SpringApplication.run(Bootstrap.class, args);
		
		if (Boolean.parseBoolean(System.getProperty("exitOnEnter", "false")))
		{
			System.out.println("\n\n\n"
					+ "*********************************************\n"
					+ "* Please press ENTER to shutdown gracefully *\n"
					+ "*********************************************");
			try (Scanner scanner = new Scanner(System.in))
			{
				if (scanner.hasNextLine())
				{
					scanner.nextLine();
				}
			}
			
			int exitCode = SpringApplication.exit(app);
			// int exitCode = SpringApplication.exit(app, () -> Integer.MAX_VALUE); // To test the error message.
			if (exitCode == 0)
			{
				System.out.println("\nThank you for letting me shutdown gracefully. Bye!");
			}
			else
			{
				System.out.println("\nThank you for letting me shutdown gracefully.");
				System.out.printf("Anyway, something went wrong, exit code: %d.\n", exitCode);
				System.out.println("Bye!");
			}
		}
	}
}
