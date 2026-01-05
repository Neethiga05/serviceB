package com.example.serviceBdemo.cli;

import com.example.serviceBdemo.ServiceBdemoApplication;
import com.example.serviceBdemo.model.PrReviewRequest;
import com.example.serviceBdemo.model.PrReviewResult;
import com.example.serviceBdemo.service.PrReviewService;
import com.example.serviceBdemo.service.github.GithubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Small CLI wrapper to run a PR review in CI and optionally post comments back to GitHub.
 * Usage: java -jar app.jar owner repo prNumber
 */
public class CliPrReviewer {
    private static final Logger log = LoggerFactory.getLogger(CliPrReviewer.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: CliPrReviewer <owner> <repo> <prNumber>");
            System.exit(2);
        }
        String owner = args[0];
        String repo = args[1];
        int prNumber;
        try {
            prNumber = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("prNumber must be an integer");
            System.exit(2);
            return;
        }

        // Start Spring context to wire beans
        ConfigurableApplicationContext ctx = SpringApplication.run(ServiceBdemoApplication.class);
        try {
            GithubClient githubClient = ctx.getBean(GithubClient.class);
            PrReviewService reviewService = ctx.getBean(PrReviewService.class);

            String diff = githubClient.fetchPullRequestDiff(owner, repo, prNumber);
            if (diff == null) {
                log.warn("Could not fetch diff for {}/{}#{}; aborting review", owner, repo, prNumber);
            } else {
                PrReviewRequest req = new PrReviewRequest();
                req.setOwner(owner);
                req.setRepo(repo);
                req.setPrNumber(prNumber);
                req.setDiff(diff);
                req.setTrigger("CI");

                PrReviewResult res = reviewService.review(req);
                System.out.println("AI Review summary:\n" + (res.getSummary() == null ? "<no summary>" : res.getSummary()));
            }
        } finally {
            ctx.close();
        }
    }
}

