CREATE TABLE change_log
(
    id          UUID,
    field       String,
    oldValue    Nullable(String),
    newValue    Nullable(String),
    entityName  String,
    entityType  String,
    entityId    Int64,

    updaterId   Int64,
    updaterName String,
    updatedDate DateTime,

    INDEX       entityIdIndex entityId TYPE bloom_filter GRANULARITY 1,
    INDEX       entityTypeIndex lowerUTF8(entityType) TYPE bloom_filter GRANULARITY 1,
    INDEX       updaterIdIndex updaterId TYPE bloom_filter GRANULARITY 1,
    INDEX       fieldIndex lowerUTF8(field) TYPE bloom_filter GRANULARITY 1

) ENGINE = ReplacingMergeTree
      PARTITION BY toYYYYMM(updatedDate)
      ORDER BY (id, updatedDate)
      SETTINGS index_granularity = 8192;