update shipment_item set types = null;

update shipment_item set types = coalesce('[{"unit": null, "count": '||trunc(qty)||', "capacity": 1, "unitName": null}]')::jsonb
where types is null and (capacity is null or capacity = 0);
update shipment_item set types = coalesce('[{"unit": null, "count": '||trunc(qty%capacity)||', "capacity": 1, "unitName": null},' ||
                                          '{"unit": "BOX", "count": '||trunc(qty/capacity)||', "capacity": '||trunc(capacity)||', "unitName": null}]')::jsonb
where types is null and qty is not null and capacity is not null and capacity > 0;

