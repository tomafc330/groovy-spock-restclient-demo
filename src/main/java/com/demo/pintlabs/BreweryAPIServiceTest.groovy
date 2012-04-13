package com.demo.pintlabs

import groovyx.net.http.ContentType;
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Timeout;

class BreweryAPIServiceTest extends Specification {

	private def api_key = 'fd7aa5a67c5dc631631ce2c6b9d07a78'
	
    def "Test getting all the beer categories"() {
        given: "there is a the service endpoint to get the information"
            def client = new RESTClient("http://api.brewerydb.com")
        when: "we list all categories"
            def params = ['key' : "${api_key}"]
            def resp = client.get(path : '/v2/categories', query : params)
        then: "we should get the list of categories"
            println (resp.data)
            assert resp.data.status == 'success'
            assert resp.data.data*.name.contains('British Origin Ales')
    }
    
	def "Test finding more information about a category"() {
		given: "we have a list of beer categories"
			def client = new RESTClient("http://api.brewerydb.com")
            def params = ['key' : "${api_key}"]
            def resp = client.get(path : '/v2/categories', query : params)
            def categoryId = resp.data.data*.id[0]
		when: "we drill down to see a specific one"
            params = [path : "/v2/category/${categoryId}", 'key' : "${api_key}"]
            resp = client.get(path : "/v2/category/${categoryId}", query : params)
		then: "we should receive info for that category"
			println (resp.data)
            assert resp.data.status == 'success'
            assert resp.data.data.name.contains('British Origin Ales')
	}
    
//    @Timeout(value = 1)
    def "Test getting beers with high abv content of 8%"() {
        when: "we call to get 8% beers"
            def client = new RESTClient("http://api.brewerydb.com")
            def params = ['key' : "${api_key}", abv : 8]
            
            def resp = logTime {
                return client.get(path: '/v2/beers', query : params)
            }
        then: "we should get all beers equal or greater to 8%"
            resp.data.data*.abv.each { 
                assert it == '8'
            }
            assert resp.data.status == 'success'
    }
    
    def logTime(Closure c) {
        def start = System.currentTimeMillis()
        def result = c()
        println ("Result in ms: ${System.currentTimeMillis() - start}")
        return result
    }
}
