package org.example.Utilities;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Entity.Order;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Class for parse the input Json file
 * @author Brahian Velazquez
 * */
public class ReadJson {
    ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LogManager.getLogger(ReadJson.class);
    /**
     * Load the json input file, it need to be and return it as a list of orders
     * If there is an error here the program will end here
     * @param fileName
     * @return List<Order>
     * */
    public List<Order> loadJsonFile(String fileName) {
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        File file = new File(fileName);
        List<Order> orders = null;

        try {
            orders = objectMapper.readValue(file, new TypeReference<List<Order>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug(" INPUT FILE NOT FOUND : " + fileName);
            logger.debug(e.getMessage());
            System.exit(0);
        }
        return orders;
    }
}
