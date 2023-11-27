package com.lotus.shortlink.admin.test;

public class UserTableShardingTest {
    public static final String SQL = "CREATE TABLE `t_user_%d` (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT,\n" +
            "  `username` varchar(256) DEFAULT NULL,\n" +
            "  `password` varchar(512) DEFAULT NULL,\n" +
            "  `real_name` varchar(256) DEFAULT NULL,\n" +
            "  `phone` varchar(128) DEFAULT NULL,\n" +
            "  `mail` varchar(512) DEFAULT NULL,\n" +
            "  `deletion_time` datetime DEFAULT NULL,\n" +
            "  `create_time` datetime DEFAULT NULL,\n" +
            "  `update_time` datetime DEFAULT NULL,\n" +
            "  `del_flag` tinyint(1) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_unique_username` (`username`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1723396792650825730 DEFAULT CHARSET=utf8mb3;";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.println(String.format((SQL + "%n"), i));
        }
    }
}
