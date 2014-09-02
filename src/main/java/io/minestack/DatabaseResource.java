package io.minestack;

import com.mongodb.ServerAddress;
import com.rabbitmq.client.Address;
import io.minestack.db.Uranium;
import lombok.extern.log4j.Log4j2;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DatabaseResource {

    public static void initDatabase() {
        if (Uranium.isNeedsInit()) {
            String hosts = System.getenv("MONGO_HOSTS");

            if (hosts == null) {
                log.error("MONGO_HOSTS is not set.");
                return;
            }
            List<ServerAddress> mongoAddresses = new ArrayList<ServerAddress>();
            for (String host : hosts.split(",")) {

                String[] info = host.split(":");
                try {
                    mongoAddresses.add(new ServerAddress(info[0], Integer.parseInt(info[1])));
                    log.info("Added Mongo Address " + host);
                } catch (UnknownHostException e) {
                    log.error("Invalid Mongo Address " + host);
                }
            }

            hosts = System.getenv("RABBITMQ_HOSTS");
            String username = System.getenv("RABBITMQ_USERNAME");
            String password = System.getenv("RABBITMQ_PASSWORD");

            List<Address> rabbitAddresses = new ArrayList<>();
            for (String host : hosts.split(",")) {
                String[] info = host.split(":");
                try {
                    rabbitAddresses.add(new Address(info[0], Integer.parseInt(info[1])));
                    log.info("Added RabbitMQ Address " + host);
                } catch (Exception e) {
                    log.error("Invalid RabbitMQ Address " + host);
                }
            }
            Uranium.initDatabase(mongoAddresses, rabbitAddresses, username, password);
        }
    }

}
