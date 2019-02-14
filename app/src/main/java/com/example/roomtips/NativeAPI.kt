package com.example.roomtips

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

private val API_QUERY_URL: String = "https://roomtips.herokuapp.com/ikea_api/resources/products?"

/**
 * @param [productInfo] JSON containing a single product's information
 *
 * @return Product object containing data from productInfo
 */
fun getProduct(productInfo: JSONObject): Product {
    val name: String = productInfo.getString("name")
    val description: String = productInfo.getString("description")
    val price: Double = productInfo.getDouble("price")
    val imgUrl: String = productInfo.getString("img_url")
    val productUrl: String = productInfo.getString("link")
    val debugTag = "getProduct"
    Log.d(debugTag, "product name: $name")
    Log.d(debugTag, "product description: $description")
    Log.d(debugTag, "product price: $price")
    Log.d(debugTag, "img url: $imgUrl")
    Log.d(debugTag, "product url: $productUrl")
    return Product(name, description, price, imgUrl, productUrl)
}

/**
 * @param [jsonString] json-like string provided as a response to an API call
 *
 * @return List of results as Product objects
 */
fun getProducts(jsonString: String): ArrayList<Product> {
    var results: ArrayList<Product> = ArrayList()

    val apiResponse = JSONArray(jsonString)
    for (i in 0 until apiResponse.length()) {
        results.add(getProduct(apiResponse.getJSONObject(i)))
    }

    return results
}

/**
 * @param [product] Product to query
 * @param [numOfProducts] Number of products to return
 * @param [minPrice] Minimum product price
 * @param [maxPrice] Maximum product price
 * @param [sort] Whether to sort by price or not
 * @param [imgSize] Size of product images (1-4)
 *
 * @return json-like string provided as a response from the API call
 */
fun getAPIResponse(product: String, numOfProducts: Int? = null, minPrice: Int? = null, maxPrice: Int? = null, sort: Boolean = false, imgSize: Int = 2): String {
    var queryUrl: String = API_QUERY_URL + "query=${product.replace(' ', '+')}"
    if(sort) queryUrl += "&sort=price"
    if(minPrice != null && maxPrice != null) queryUrl += "&min_price=${minPrice}&max_price=${maxPrice}"
    if(numOfProducts != null) queryUrl += "&num=${numOfProducts}"
    queryUrl += "&img_size=${imgSize}"
    Log.d("getAPIResponse", "hitting endpoint: ${queryUrl}")

    return URL(queryUrl).readText()
}

/**
 * @param [product] Product to query
 * @param [numOfProducts] Number of products to return
 * @param [minPrice] Minimum product price
 * @param [maxPrice] Maximum product price
 * @param [sort] Whether to sort by price or not
 * @param [imgSize] Size of product images (1-4)
 *
 * @return List of results as Product objects
 */
fun getSuggestionsIkea(product: String, numOfProducts: Int? = null, minPrice: Int? = null, maxPrice: Int? = null, sort: Boolean = false, imgSize: Int = 2): ArrayList<Product> {
    val response = getAPIResponse(product, numOfProducts, minPrice, maxPrice, sort, imgSize)
    return getProducts(response)
}