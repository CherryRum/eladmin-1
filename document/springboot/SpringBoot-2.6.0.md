---
在Spring Boot 2.6.0版本，启动报错PatternsRequestCondition.getPatterns()空指针，原因详见springfox的[issues](https://github.com/springfox/springfox/issues/3462) ，扩展 [URL Matching with PathPattern in Spring MVC](https://spring.io/blog/2020/06/30/url-matching-with-pathpattern-in-spring-mvc) 。该版本Spring boot的 [ Release-Notes ](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes)

导致报错的原因是：
- 在SpringMVC的5.3.x系列版本（Spring Boot 2.6.x），引入新的URL Matching方式PathPattern。之前已有的是AntPathMatcher。
- 在Spring Boot 2.6.0版本，将默认的调整为PathPattern。并提供配置 `spring.mvc.pathmatch.matching-strategy=ant_path_matcher` 可以切换回AntPathMatcher，但是`The actuator endpoints now also use PathPattern based URL matching. Note that the path matching strategy for actuator endpoints is not configurable via a configuration property.`
- 导致报错的就是webEndpointServletHandlerMapping方法的`/actuator/health/**、/actuator/health、/actuator`这几个方法。所以在找到让springfox忽略（不处理）这几个方法的方案前。还未找到好的解决方案
- ~~暂通过改源码解决，期待后续方案。📦后的jar详见ex-lib目录~~。https://github.com/lWoHvYe/springfox/commit/9cb5e727a48e815b73461793ad37eae73c4af0e7
- 生活总是充满惊喜。上面说了，导致问题的原因是/actuator/**，这些是actuator模块的，项目并未显式的引用，所以为神马会有这几个path？ 🤪答案就是redisson。排除掉就可以了，至少只要不需要这些功能，不用改源码


⌚️马上🕑了。天亮再继续。考虑从springfox迁移到springdoc了

https://github.com/spring-projects/spring-boot/issues/24645

https://github.com/spring-projects/spring-boot/issues/24805

https://github.com/spring-projects/spring-boot/issues/21694

https://github.com/spring-projects/spring-framework/issues/24952

https://stackoverflow.com/questions/69108273/spring-boot-swagger-documentation-doesnt-work/69814964

If one insists on continuing to use Springfox with Spring Boot >= 2.6, one can try to force use of Ant Path Matching by setting
```yaml
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

Forcing Ant Path Matching on the actuators is a separate problem. It works by injecting the WebMvcEndpointHandlerMapping that was auto-configured before the change by WebMvcEndpointManagementContextConfiguration:
```java
@Bean
public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
    WebEndpointsSupplier webEndpointsSupplier,
    ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
    EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
    WebEndpointProperties webEndpointProperties, Environment environment) {
  List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
  Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
  allEndpoints.addAll(webEndpoints);
  allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
  allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
  String basePath = webEndpointProperties.getBasePath();
  EndpointMapping endpointMapping = new EndpointMapping(basePath);
  boolean shouldRegisterLinksMapping = shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
  return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
      corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
      shouldRegisterLinksMapping);
}

private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment,
    String basePath) {
  return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
      || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
}
```
There may be a cleverer way by excluding the actuators from being analyzed by Springfox in the first place.

You're mileage may vary. Switching to springdoc is probably the more worthwhile approach.

---