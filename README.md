# Hibernate Test Case Templates: ORM

This testcase show that when using hibernate with jcache and ehcache,
session.delete does not trigger cache eviction - unless the entity is defined
as cache usage="transactional"
