# Tạo database và các bảng trên click house 
CREATE DATABASE IF NOT EXISTS adnlog_db;

USE adnlog_db;

CREATE TABLE IF NOT EXISTS adnlog_db.campaign_events
(
    campaignId UInt64,          
    click_or_view Boolean,
    event_date Date,
    guid UInt64
)
ENGINE = MergeTree()
ORDER BY (event_date, campaignId);

CREATE TABLE IF NOT EXISTS adnlog_db.banner_events
(
    bannerId UInt64,          
    click_or_view Boolean,
    event_date Date,
    guid UInt64
)
ENGINE = MergeTree()
ORDER BY (event_date, bannerId);
