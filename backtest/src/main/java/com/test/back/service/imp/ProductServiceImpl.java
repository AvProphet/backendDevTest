package com.test.back.service.imp;

import com.google.gson.Gson;
import com.test.back.config.HttpConfig;
import com.test.back.dto.ProductDto;
import com.test.back.exception.NotFoundException;
import com.test.back.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final HttpConfig httpConfig;
    private static final Gson gson = new Gson();
    private static final CloseableHttpClient httpclient = HttpClients.createDefault();

    @Autowired
    public ProductServiceImpl(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    @Override
    public List<ProductDto> getSimilarProducts(String productId) {
        List<Integer> similarIds = getSimilarIds(productId);

        return getProducts(similarIds);
    }

    private List<Integer> getSimilarIds(String productId) {
        String URL_SIMILAR_IDS = httpConfig.getUrl(HttpConfig.Endpoint.SIMILAR_IDS);
        System.out.println(URL_SIMILAR_IDS);

        //Providing replacement for a pathVariable in the URL
        URL_SIMILAR_IDS = URL_SIMILAR_IDS.replace("{productId}", URLEncoder.encode(productId, StandardCharsets.UTF_8));

        Integer[] idsList = new Integer[3];

        HttpGet getRequest = new HttpGet(URL_SIMILAR_IDS);
        try (CloseableHttpResponse responseBody = httpclient.execute(getRequest)) {
            HttpEntity responseEntity = responseBody.getEntity();

            String result = EntityUtils.toString(responseEntity);
            int statusCode = responseBody.getCode();


            if (statusCode != HttpStatus.SC_OK) {
                throw new NotFoundException();
            } else {
                idsList = gson.fromJson(result, Integer[].class);

                log.info("Successfully retrieved ids from mock");

                //ensure it is fully consumed
                EntityUtils.consume(responseEntity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return Arrays.stream(idsList).collect(Collectors.toList());
    }

    private List<ProductDto> getProducts(List<Integer> similarIds) {
        List<ProductDto> similarProductsList = new ArrayList<>();

        for (Integer id : similarIds) {
            String URL_PRODUCT = httpConfig.getUrl(HttpConfig.Endpoint.PRODUCT_URL);

            URL_PRODUCT = URL_PRODUCT.replace("{productId}", URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8));

            HttpGet getRequest = new HttpGet(URL_PRODUCT);
            ProductDto productDto;
            try (CloseableHttpResponse responseBody = httpclient.execute(getRequest)) {
                HttpEntity responseEntity = responseBody.getEntity();

                String result = EntityUtils.toString(responseEntity);
                int statusCode = responseBody.getCode();

                productDto = gson.fromJson(result, ProductDto.class);

                //In case we have one null object, and the next one is not null, we're just skipping this value, otherwise, return the entire list
                if (statusCode != HttpStatus.SC_OK) {
                    if (similarIds.iterator().hasNext()) {
                        log.info("Skipping null object to the next value");
                    } else {
                        return similarProductsList;
                    }
                } else {
                    log.info("Successfully retrieved product " + productDto + " from mock");

                    similarProductsList.add(productDto);

                    EntityUtils.consume(responseEntity);
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
        return similarProductsList;
    }
}
