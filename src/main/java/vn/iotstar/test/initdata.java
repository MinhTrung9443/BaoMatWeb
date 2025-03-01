// package vn.iotstar.test;
//
//
//import vn.iotstar.entity.Category;
//import vn.iotstar.entity.Product;
// import vn.iotstar.repository.IProductRepository;
// import com.google.gson.*;
// import jakarta.annotation.PostConstruct;
// import org.springframework.beans.BeanUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
//
// import java.io.FileInputStream;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
//
// @Component
// public class initdata {
//     @Autowired
//     private  IProductRepository productRepository;
//
//     @PostConstruct
//     public void init() {
//         try {
//        	JsonElement root = JsonParser.parseReader(new InputStreamReader(new FileInputStream("D:\\WellnessVietnamese.json"), StandardCharsets.UTF_8));
//             parseJson(root);
//             root = JsonParser.parseReader(new InputStreamReader(new FileInputStream("D:\\SkinVietnamese.json"), StandardCharsets.UTF_8));
//             parseJson(root);
//             root = JsonParser.parseReader(new InputStreamReader(new FileInputStream("D:\\MakeUpVietnamese.json"), StandardCharsets.UTF_8));
//             parseJson(root);
//             root = JsonParser.parseReader(new InputStreamReader(new FileInputStream("D:\\HairVietnamese.json"), StandardCharsets.UTF_8));
//             parseJson(root);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
//
//     public void parseJson(JsonElement element) {
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//         List<Product> products = new ArrayList<>();
//         JsonArray array = element.getAsJsonArray();
//
//         for (JsonElement arrayElement : array) {
//             JsonObject product = arrayElement.getAsJsonObject();
//             Product product1 = new Product();
//             product1.setProductName(product.get("name").getAsString());
//             product1.setPrice(product.get("cost").getAsInt());
//             product1.setDescription(product.get("description").getAsString());
//             product1.setBrand(product.get("brand").getAsString());
//             product1.setIngredient(product.get("ingredient").getAsString());
//             product1.setVolumeOrWeight(product.get("volume").getAsString());
//             product1.setImages(product.get("image").getAsString());
//             product1.setStock(100);
//             products.add(product1);
//         }
//         List<Product> lst = new ArrayList<>();
//
//         products.forEach(product -> {
//        	 Product product1 = new Product();
//             BeanUtils.copyProperties(product, product1);
//             productRepository.save(product1);
//         });
//     }
// }
//
//
//
