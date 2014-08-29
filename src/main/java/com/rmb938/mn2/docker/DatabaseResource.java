package com.rmb938.mn2.docker;

import com.mongodb.ServerAddress;
import com.rabbitmq.client.Address;
import com.rmb938.mn2.docker.db.database.*;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import com.rmb938.mn2.docker.db.rabbitmq.RabbitMQ;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DatabaseResource {

    private static DatabaseResource databaseResource;

    @Getter
    private static NodeLoader nodeLoader;

    @Getter
    private static BungeeLoader bungeeLoader;

    @Getter
    private static BungeeTypeLoader bungeeTypeLoader;

    @Getter
    private static ServerTypeLoader serverTypeLoader;

    @Getter
    private static ServerLoader serverLoader;

    @Getter
    private static PluginLoader pluginLoader;

    @Getter
    private static WorldLoader worldLoader;

    public static void initDatabase() {
        if (databaseResource == null) {
            databaseResource = new DatabaseResource();
        }
    }

    private DatabaseResource() {
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

        if (mongoAddresses.isEmpty()) {
            log.error("No valid mongo addresses");
            return;
        }
        log.info("Setting up mongo database mn2");
        MongoDatabase mongoDatabase = new MongoDatabase(mongoAddresses, "mn2");

        hosts = System.getenv("RABBITMQ_HOSTS");
        String username = System.getenv("RABBITMQ_USERNAME");
        String password = System.getenv("RABBITMQ_PASSWORD");

        List<Address> rabbitAddresses = new ArrayList<>();
        for (String host : hosts.split(",")) {
            String[] info = host.split(":");
            try {
                rabbitAddresses.add(new Address(info[0], Integer.parseInt(info[1])));
                log.info("Added RabbitMQ Address "+host);
            } catch (Exception e) {
                log.error("Invalid RabbitMQ Address " + host);
            }
        }

        if (rabbitAddresses.isEmpty()) {
            log.error("No valid RabbitMQ addresses");
            return;
        }

        RabbitMQ rabbitMQ = null;
        try {
            log.info("Setting up RabbitMQ");
            rabbitMQ = new RabbitMQ(rabbitAddresses, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pluginLoader = new PluginLoader(mongoDatabase);
        worldLoader = new WorldLoader(mongoDatabase);
        serverTypeLoader = new ServerTypeLoader(mongoDatabase, pluginLoader, worldLoader);
        bungeeTypeLoader = new BungeeTypeLoader(mongoDatabase, pluginLoader, serverTypeLoader);
        nodeLoader = new NodeLoader(mongoDatabase, bungeeTypeLoader);
        bungeeLoader = new BungeeLoader(mongoDatabase, bungeeTypeLoader, nodeLoader);
        serverLoader = new ServerLoader(mongoDatabase, nodeLoader, serverTypeLoader);
    }

}
