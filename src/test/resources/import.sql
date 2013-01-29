-- insert test users
INSERT INTO `user` VALUES (1,'testuser1@test.com','user1','user1','7xyb63kC2ILEWopdoLiakOW4s8C9H5j/','ADMIN_USER');
INSERT INTO `project` (`projectid`, `project_number`, `description`) VALUES ('1', '1', 'test');
--INSERT INTO `project` (`projectid`, `project_number`, `description`) VALUES ('2', '2', 'test2');
--INSERT INTO `project` (`projectid`, `project_number`) VALUES ('3', '3');
INSERT INTO matrix_dimension VALUES (1,'2D'),(2,'3D');
INSERT INTO `instrument`VALUES (1, 'generic microscope');
INSERT INTO `magnification` VALUES (1, '10x');
INSERT INTO plate_format VALUES (1,96,12,8,8991.88),(2,48,8,6,13440.5),(3,24,6,4,19749.4),(4,12,4,3,27545.4),(5,6,3,2,40140.4);
INSERT INTO assay VALUES (1,'ORIS',1),(2,'random seeding',1),(3,'ORIS',2),(4,'mixed',2),(5,'MCTS',2);
INSERT INTO assay_medium VALUES (1,'DMEM','FBS',10),(15,'DMEM','FBS hi',10),(16,'DMEM','FBS hi',10),(17,'DMEM','FBS hi',10),(18,'DMEM','FBS hi',10),(19,'DMEM','FBS hi',10),(20,'DMEM','FBS hi',10),(21,'DMEM','FBS hi',10),(22,'DMEM','FBS hi',10),(23,'DMEM','FBS hi',10),(24,'DMEM','FBS hi',10),(25,'DMEM','FBS hi',10),(26,'DMEM','FBS hi',10),(27,'DMEM','FBS hi',10),(28,'DMEM','FBS hi',10),(29,'DMEM','FBS hi',10),(30,'DMEM','FBS hi',10),(31,'DMEM','FBS hi',10),(32,'DMEM','FBS hi',10),(33,'DMEM','FBS hi',10),(34,'DMEM','FBS hi',10),(35,'DMEM','FBS hi',10),(36,'DMEM','FBS hi',10),(37,'DMEM','FBS hi',10),(38,'DMEM','FBS hi',10),(39,'DMEM','FBS hi',10),(40,'DMEM','FBS hi',10),(41,'DMEM','FBS hi',10),(42,'DMEM','FBS hi',10),(43,'DMEM','FBS hi',10),(44,'DMEM','FBS hi',10);
INSERT INTO cell_line_type VALUES (5,'A431'),(4,'BT-549'),(3,'HT-1080'),(2,'MCF-7'),(1,'MDA-MB-231');
INSERT INTO cell_line VALUES (1, 'day -1', 4500,'DMEM','FBS hi',10,1),(2,'day -1', 4500,'RPMI 1640','FCS hi',10,2),(3,'day -1', 4500,'DMEM/F12','HS hi',10,3),(4,'day -1', 4500,'MEM','FBS hni',10,4),(5,'day -1',4500,'MEM alpha','FCS hni',10,5),(6,'day -1', 4500,'Ham F12','HS hni',10,1);

--INSERT INTO ecm_coating VALUES (1,'monomeric coating'),(2,'thin gel coating');
--INSERT INTO ecm_composition VALUES (1,'Collagen I (bovine)',1),(2,'Collagen I (human)',1),(3,'Collagen I (rat tail)',2),(4,'Laminin I (mouse)',1),(5,'Laminin I (mouse)',2),(6,'Vitronectin (human)',1),(7,'BD Matrigel (mouse)',2),(8,'Collagen IV (human)',1),(9,'Collagen IV (mouse)',1),(10,'Fibronectin (human)',1),(11,'Fibronectin (bovine)',1),(12,'Nutragen',2),(14,'Collagen I (rat tail)',1);
--INSERT INTO ecm_density VALUES (1,1),(2,2),(3,4);
--INSERT INTO ecm VALUES (1,0.5, 0.5, '12 h','37 C',NULL, NULL, 1,1,1,NULL, 'mg/ml');

