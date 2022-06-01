package com.igevin.trywithlock;

import com.igevin.trywithlock.usage.ConcurrentTaskRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class TryWithLockExampleApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(TryWithLockExampleApplication.class, args);
		ExecutorService executor = createExecutor();
		ConcurrentTaskRunner runner = new ConcurrentTaskRunner(executor);

		runner.resetVisitCounter().oneThreadVisitCount();
		runner.resetVisitCounter().unsafeVisitCount();
//		runner.resetVisitCounter().atomicVisitCount();
		runner.resetVisitCounter().safeVisitCount();
		runner.resetVisitCounter().safeVisitCount2();
		runner.resetVisitCounter().safeVisitCount3();

		executor.shutdown();
	}

	private static ExecutorService createExecutor() {
		int core = Runtime.getRuntime().availableProcessors();
		return Executors.newFixedThreadPool(core + 1);
	}

}
