package com.test.back.service.imp;

import com.google.gson.Gson;
import com.test.back.dto.ProductDto;
import com.test.back.exception.NotFoundException;
import com.test.back.service.ProductService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    static final Gson gson = new Gson();
    static final CloseableHttpClient httpclient = HttpClients.createDefault();

    @Override
    public List<ProductDto> getSimilarProducts(String productId) {
        List<Integer> similarIds = getSimilarIds(productId);

        return getProducts(similarIds);
    }

    private List<Integer> getSimilarIds(String productId) {
        String URL_SIMILAR_IDS = "http://localhost:3001/product/{productId}/similarids";

        //Providing replacement for a pathVariable in the URL
        try {
            URL_SIMILAR_IDS = URL_SIMILAR_IDS.replace("{productId}", URLEncoder.encode(productId, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Integer[] idsList = new Integer[3];

        HttpGet getRequest = new HttpGet(URL_SIMILAR_IDS);
        try (CloseableHttpResponse responseBody = httpclient.execute(getRequest)) {
            HttpEntity responseEntity = responseBody.getEntity();

            String result = EntityUtils.toString(responseEntity);
            System.out.println(result);
            if (result.equals("Not Found")) {
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
            String URL_PRODUCT = "http://localhost:3001/product/{productId}";

            try {
                URL_PRODUCT = URL_PRODUCT.replace("{productId}", URLEncoder.encode(String.valueOf(id), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpGet getRequest = new HttpGet(URL_PRODUCT);
            ProductDto productDto;
            try (CloseableHttpResponse responseBody = httpclient.execute(getRequest)) {
                HttpEntity responseEntity = responseBody.getEntity();

                String result = EntityUtils.toString(responseEntity);
                productDto = gson.fromJson(result, ProductDto.class);

                if (Objects.isNull(productDto)) {
                    return similarProductsList;
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
