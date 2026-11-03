package uz.uzkassa.silen.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.silen.domain.*;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration implements CacheConstants{

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, AcceptanceTransferAct.class.getName());
            createCache(cm, AcceptanceTransferAct.class.getName() + ".products");
            createCache(cm, AcceptanceTransferActProduct.class.getName());
            createCache(cm, Aggregation.class.getName());
            createCache(cm, AggregationHistory.class.getName());
            createCache(cm, Attributes.class.getName());
            createCache(cm, Attributes.class.getName() + ".children");
            createCache(cm, Attributes.class.getName() + ".products");
            createCache(cm, Attributes.class.getName() + ".productTypes");
            createCache(cm, Bank.class.getName());
            createCache(cm, BatchAggregation.class.getName());
            createCache(cm, Changelog.class.getName());
            createCache(cm, Contract.class.getName());
            createCache(cm, Customer.class.getName());
            createCache(cm, Customer.class.getName() + ".contracts");
            createCache(cm, CustomerBranch.class.getName());
            createCache(cm, CustomerProduct.class.getName());
            createCache(cm, District.class.getName());
            createCache(cm, Dropout.class.getName());
            createCache(cm, Excise.class.getName());
            createCache(cm, ExportReport.class.getName());
            createCache(cm, FacturaHost.class.getName());
            createCache(cm, GivePermission.class.getName());
            createCache(cm, Invoice.class.getName());
            createCache(cm, Invoice.class.getName() + ".items");
            createCache(cm, ListInvoice.class.getName());
            createCache(cm, Measure.class.getName());
            createCache(cm, Notification.class.getName());
            createCache(cm, NotificationTemplate.class.getName());
            createCache(cm, Order.class.getName());
            createCache(cm, OrderProduct.class.getName());
            createCache(cm, Organization.class.getName());
            createCache(cm, OrganizationPermissions.class.getName());
            createCache(cm, Party.class.getName());
            createCache(cm, PartyAggregation.class.getName());
            createCache(cm, Permission.class.getName());
            createCache(cm, Permission.class.getName() + ".children");
            createCache(cm, Product.class.getName());
            createCache(cm, ProductAttributes.class.getName());
            createCache(cm, PurchaseOrder.class.getName());
            createCache(cm, PurchaseOrder.class.getName() + ".shipments");
            createCache(cm, PurchaseOrder.class.getName() + ".products");
            createCache(cm, PurchaseOrderProduct.class.getName());
            createCache(cm, RolePermission.class.getName());
            createCache(cm, Reference.class.getName());
            createCache(cm, Region.class.getName());
            createCache(cm, Shipment.class.getName());
            createCache(cm, Shipment.class.getName() + ".invoices");
            createCache(cm, Shipment.class.getName() + ".acts");
            createCache(cm, Shipment.class.getName() + ".items");
            createCache(cm, ShipmentItem.class.getName());
            createCache(cm, Stock.class.getName());
            createCache(cm, Store.class.getName());
            createCache(cm, StoreIn.class.getName());
            createCache(cm, StoreIn.class.getName() + ".invoices");
            createCache(cm, StoreIn.class.getName() + ".storeInItems");
            createCache(cm, StoreInItem.class.getName());
            createCache(cm, User.class.getName());
            createCache(cm, VatRate.class.getName());
            createCache(cm, Warehouse.class.getName());
            createCache(cm, PartyAggregation.class.getName());
            createCache(cm, Waybill.class.getName());
            createCache(cm, Waybill.class.getName() + ".executors");
            createCache(cm, WaybillTransport.class.getName());
            createCache(cm, WaybillTransport.class.getName() + ".carriages");
            createCache(cm, WaybillTransport.class.getName() + ".productGroups");
            createCache(cm, WaybillProductGroups.class.getName());
            createCache(cm, WaybillProductGroups.class.getName() + ".products");
            createCache(cm, WaybillProduct.class.getName());
            createCache(cm, WaybillExecutor.class.getName());
            createCache(cm, User.class.getName() + ".authorities");
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(1000))
                            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(86400))).build()));
        }
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}
