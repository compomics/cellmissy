-- create tables
    create table algorithm (
        algorithmid bigint not null auto_increment,
        algorithm_name varchar(255) not null,
        primary key (algorithmid)
    ) ENGINE=InnoDB;

    create table assay (
        assayid bigint not null auto_increment,
        assay_type varchar(255),
        l_matrix_dimensionid bigint not null,
        primary key (assayid)
    ) ENGINE=InnoDB;

    create table assay_medium (
        assay_mediumid bigint not null auto_increment,
        medium varchar(255),
        serum varchar(255),
        serum_concentration double precision,
        volume double precision,
        primary key (assay_mediumid)
    ) ENGINE=InnoDB;

    create table bottom_matrix (
        bottom_matrixid bigint not null auto_increment,
        type varchar(255),
        primary key (bottom_matrixid)
    ) ENGINE=InnoDB;

    create table cell_line (
        cell_lineid bigint not null auto_increment,
        growth_medium varchar(255),
        seeding_density integer,
        seeding_time varchar(255),
        serum varchar(255),
        serum_concentration double precision,
        l_cell_line_typeid bigint not null,
        primary key (cell_lineid)
    ) ENGINE=InnoDB;

    create table cell_line_type (
        cell_line_typeid bigint not null auto_increment,
        name varchar(255) not null unique,
        primary key (cell_line_typeid)
    ) ENGINE=InnoDB;

    create table ecm (
        ecmid bigint not null auto_increment,
        bottom_matrix_volume double precision,
        coating_temperature varchar(255),
        coating_time varchar(255),
        concentration double precision,
        concentration_unit varchar(255),
        polymerisation_ph varchar(255),
        polymerisation_temperature varchar(255),
        polymerisation_time varchar(255),
        top_matrix_volume double precision,
        volume double precision,
        l_bottom_matrixid bigint,
        l_composition_typeid bigint not null,
        l_ecm_densityid bigint,
        primary key (ecmid)
    ) ENGINE=InnoDB;

    create table ecm_composition (
        composition_typeid bigint not null auto_increment,
        composition_type varchar(255),
        l_matrix_dimensionid bigint not null,
        primary key (composition_typeid)
    ) ENGINE=InnoDB;

    create table ecm_density (
        ecm_densityid bigint not null auto_increment,
        ecm_density double precision,
        primary key (ecm_densityid)
    ) ENGINE=InnoDB;

    create table experiment (
        experimentid bigint not null auto_increment,
        duration double precision,
        experiment_date datetime,
        experiment_interval double precision,
        experiment_number integer not null,
        experiment_status varchar(255) not null,
        purpose varchar(150),
        time_frames integer,
        l_instrumentid bigint not null,
        l_magnificationid bigint not null,
        l_plate_formatid bigint not null,
        l_projectid bigint not null,
        l_userid bigint not null,
        primary key (experimentid)
    ) ENGINE=InnoDB;

    create table imaging_type (
        imaging_typeid bigint not null auto_increment,
        exposure_time double precision,
        light_intensity double precision,
        name varchar(255),
        primary key (imaging_typeid)
    ) ENGINE=InnoDB;

    create table instrument (
        instrumentid bigint not null auto_increment,
        conversion_factor double precision not null,
        name varchar(255) not null unique,
        primary key (instrumentid),
        unique (name)
    ) ENGINE=InnoDB;

    create table magnification (
        magnificationid bigint not null auto_increment,
        magnification_number varchar(255),
        primary key (magnificationid)
    ) ENGINE=InnoDB;

    create table matrix_dimension (
        matrix_dimensionid bigint not null,
        dimension varchar(255),
        primary key (matrix_dimensionid)
    ) ENGINE=InnoDB;

    create table plate_condition (
        plate_conditionid bigint not null auto_increment,
        l_assayid bigint not null,
        l_assay_mediumid bigint not null,
        l_cell_lineid bigint not null,
        l_ecmid bigint not null,
        l_experimentid bigint not null,
        primary key (plate_conditionid),
        unique (l_assay_mediumid),
        unique (l_cell_lineid)
    ) ENGINE=InnoDB;

    create table plate_format (
        plate_formatid bigint not null auto_increment,
        format integer not null,
        number_of_cols integer,
        number_of_rows integer,
        well_size double precision,
        primary key (plate_formatid)
    ) ENGINE=InnoDB;

    create table project (
        projectid bigint not null auto_increment,
        description varchar(255),
        project_number integer not null unique,
        primary key (projectid),
        unique (project_number)
    ) ENGINE=InnoDB;

    create table project_has_user (
        project_has_userid bigint not null auto_increment,
        l_projectid bigint,
        l_userid bigint,
        primary key (project_has_userid)
    ) ENGINE=InnoDB;

    create table time_step (
        time_stepid bigint not null auto_increment,
        area double precision not null,
        centroid_x double precision,
        centroid_y double precision,
        eccentricity double precision,
        major_axis double precision,
        minor_axis double precision,
        time_step_sequence integer not null,
        l_well_has_imaging_typeid bigint not null,
        primary key (time_stepid)
    ) ENGINE=InnoDB;

    create table track (
        trackid bigint not null auto_increment,
        track_length integer not null,
        track_number integer not null,
        l_well_has_imaging_typeid bigint not null,
        primary key (trackid)
    ) ENGINE=InnoDB;

    create table track_point (
        track_pointid bigint not null auto_increment,
        angle double precision,
        angle_delta double precision,
        cell_col double precision not null,
        cell_row double precision not null,
        cumulated_distance double precision,
        distance double precision,
        motion_consistency double precision,
        relative_angle double precision,
        time_index integer not null,
        velocity_pixels double precision,
        l_trackid bigint not null,
        primary key (track_pointid)
    ) ENGINE=InnoDB;

    create table treatment (
        treatmentid bigint not null auto_increment,
        concentration double precision,
        concentration_unit varchar(255),
        drug_solvent varchar(255),
        drug_solvent_concentration double precision,
        timing varchar(255),
        l_plate_conditionid bigint,
        l_treatment_typeid bigint,
        primary key (treatmentid)
    ) ENGINE=InnoDB;

    create table treatment_type (
        treatment_typeid bigint not null auto_increment,
        name varchar(255) not null unique,
        treatment_category integer not null,
        primary key (treatment_typeid)
    ) ENGINE=InnoDB;

    create table user (
        userid bigint not null auto_increment,
        email varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        password varchar(40) not null,
        role varchar(255) not null,
        primary key (userid),
        unique (first_name, last_name)
    ) ENGINE=InnoDB;

    create table well (
        wellid bigint not null auto_increment,
        column_number integer,
        rownumber integer,
        l_conditionid bigint not null,
        primary key (wellid)
    ) ENGINE=InnoDB;

    create table well_has_imaging_type (
        well_has_imaging_typeid bigint not null auto_increment,
        sequence_number integer,
        x_coordinate double precision,
        y_coordinate double precision,
        l_algorithmid bigint,
        l_imaging_typeid bigint,
        l_wellid bigint,
        primary key (well_has_imaging_typeid)
    ) ENGINE=InnoDB;

    alter table assay 
        add index FK58CEA79A0B91A7F (l_matrix_dimensionid), 
        add constraint FK58CEA79A0B91A7F 
        foreign key (l_matrix_dimensionid) 
        references matrix_dimension (matrix_dimensionid);

    alter table cell_line 
        add index FK61276CB1F2972C42 (l_cell_line_typeid), 
        add constraint FK61276CB1F2972C42 
        foreign key (l_cell_line_typeid) 
        references cell_line_type (cell_line_typeid);

    alter table ecm 
        add index FK1878FA9ECB305 (l_bottom_matrixid), 
        add constraint FK1878FA9ECB305 
        foreign key (l_bottom_matrixid) 
        references bottom_matrix (bottom_matrixid);

    alter table ecm 
        add index FK1878F1BA3B8F5 (l_ecm_densityid), 
        add constraint FK1878F1BA3B8F5 
        foreign key (l_ecm_densityid) 
        references ecm_density (ecm_densityid);

    alter table ecm 
        add index FK1878F6A4B8334 (l_composition_typeid), 
        add constraint FK1878F6A4B8334 
        foreign key (l_composition_typeid) 
        references ecm_composition (composition_typeid);

    alter table ecm_composition 
        add index FK30B10FAA0B91A7F (l_matrix_dimensionid), 
        add constraint FK30B10FAA0B91A7F 
        foreign key (l_matrix_dimensionid) 
        references matrix_dimension (matrix_dimensionid);

    alter table experiment 
        add index FKFAE9DBFDDC95B89E (l_projectid), 
        add constraint FKFAE9DBFDDC95B89E 
        foreign key (l_projectid) 
        references project (projectid);

    alter table experiment 
        add index FKFAE9DBFD80B089F8 (l_instrumentid), 
        add constraint FKFAE9DBFD80B089F8 
        foreign key (l_instrumentid) 
        references instrument (instrumentid);

    alter table experiment 
        add index FKFAE9DBFD5592DF (l_plate_formatid), 
        add constraint FKFAE9DBFD5592DF 
        foreign key (l_plate_formatid) 
        references plate_format (plate_formatid);

    alter table experiment 
        add index FKFAE9DBFD735165B2 (l_magnificationid), 
        add constraint FKFAE9DBFD735165B2 
        foreign key (l_magnificationid) 
        references magnification (magnificationid);

    alter table experiment 
        add index FKFAE9DBFD3CBF3800 (l_userid), 
        add constraint FKFAE9DBFD3CBF3800 
        foreign key (l_userid) 
        references user (userid);

    alter table plate_condition 
        add index FKA7D8DF326454237B (l_assay_mediumid), 
        add constraint FKA7D8DF326454237B 
        foreign key (l_assay_mediumid) 
        references assay_medium (assay_mediumid);

    alter table plate_condition 
        add index FKA7D8DF3238CF4F9E (l_assayid), 
        add constraint FKA7D8DF3238CF4F9E 
        foreign key (l_assayid) 
        references assay (assayid);

    alter table plate_condition 
        add index FKA7D8DF324B5F5C8A (l_ecmid), 
        add constraint FKA7D8DF324B5F5C8A 
        foreign key (l_ecmid) 
        references ecm (ecmid);

    alter table plate_condition 
        add index FKA7D8DF32D2EBCCA4 (l_experimentid), 
        add constraint FKA7D8DF32D2EBCCA4 
        foreign key (l_experimentid) 
        references experiment (experimentid);

    alter table plate_condition 
        add index FKA7D8DF32DFC0BB8B (l_cell_lineid), 
        add constraint FKA7D8DF32DFC0BB8B 
        foreign key (l_cell_lineid) 
        references cell_line (cell_lineid);

    alter table project_has_user 
        add index FKC18AF4B6DC95B89E (l_projectid), 
        add constraint FKC18AF4B6DC95B89E 
        foreign key (l_projectid) 
        references project (projectid);

    alter table project_has_user 
        add index FKC18AF4B63CBF3800 (l_userid), 
        add constraint FKC18AF4B63CBF3800 
        foreign key (l_userid) 
        references user (userid);

    alter table time_step 
        add index FK22F24FE9EA84F21 (l_well_has_imaging_typeid), 
        add constraint FK22F24FE9EA84F21 
        foreign key (l_well_has_imaging_typeid) 
        references well_has_imaging_type (well_has_imaging_typeid);

    alter table track 
        add index FK697F14B9EA84F21 (l_well_has_imaging_typeid), 
        add constraint FK697F14B9EA84F21 
        foreign key (l_well_has_imaging_typeid) 
        references well_has_imaging_type (well_has_imaging_typeid);

    alter table track_point 
        add index FK5F0D459C243EF0C2 (l_trackid), 
        add constraint FK5F0D459C243EF0C2 
        foreign key (l_trackid) 
        references track (trackid);

    alter table treatment 
        add index FKFC39787857D45365 (l_treatment_typeid), 
        add constraint FKFC39787857D45365 
        foreign key (l_treatment_typeid) 
        references treatment_type (treatment_typeid);

    alter table treatment 
        add index FKFC397878378D253B (l_plate_conditionid), 
        add constraint FKFC397878378D253B 
        foreign key (l_plate_conditionid) 
        references plate_condition (plate_conditionid);

    alter table well 
        add index FK37A0CE2EA0D2A4 (l_conditionid), 
        add constraint FK37A0CE2EA0D2A4 
        foreign key (l_conditionid) 
        references plate_condition (plate_conditionid);

    alter table well_has_imaging_type 
        add index FK5726DC97FCE1ED8A (l_algorithmid), 
        add constraint FK5726DC97FCE1ED8A 
        foreign key (l_algorithmid) 
        references algorithm (algorithmid);

    alter table well_has_imaging_type 
        add index FK5726DC9783E00125 (l_imaging_typeid), 
        add constraint FK5726DC9783E00125 
        foreign key (l_imaging_typeid) 
        references imaging_type (imaging_typeid);

    alter table well_has_imaging_type 
        add index FK5726DC973F676D46 (l_wellid), 
        add constraint FK5726DC973F676D46 
        foreign key (l_wellid) 
        references well (wellid);
		
-- insertions
INSERT INTO `bottom_matrix` (`bottom_matrixid`, `type`) VALUES ('1', 'thin gel coating');
INSERT INTO `bottom_matrix` (`bottom_matrixid`, `type`) VALUES ('2', 'gel');

INSERT INTO `cell_line_type` (`cell_line_typeid`, `name`) VALUES ('1', 'MDA-MB-231');
INSERT INTO `cell_line_type` (`cell_line_typeid`, `name`) VALUES ('2', 'MCF-7');
INSERT INTO `cell_line_type` (`cell_line_typeid`, `name`) VALUES ('3', 'HT-1080');
INSERT INTO `cell_line_type` (`cell_line_typeid`, `name`) VALUES ('4', 'BT-549');
INSERT INTO `cell_line_type` (`cell_line_typeid`, `name`) VALUES ('5', 'A431');

INSERT INTO `cell_line` (`cell_lineid`, `growth_medium`, `serum`, `l_cell_line_typeid`) VALUES ('1', 'DMEM', 'FBS hi', '1');
INSERT INTO `cell_line` (`cell_lineid`, `growth_medium`, `serum`, `l_cell_line_typeid`) VALUES ('2', 'RPMI 1640', 'FCS hi', '1');
INSERT INTO `cell_line` (`cell_lineid`, `growth_medium`, `serum`, `l_cell_line_typeid`) VALUES ('3', 'DMEM', 'FBS non-hi', '1');
INSERT INTO `cell_line` (`cell_lineid`, `growth_medium`, `serum`, `l_cell_line_typeid`) VALUES ('4', 'RPMI 1640', 'FCS non-hi', '1');

INSERT INTO `matrix_dimension` (`matrix_dimensionid`, `dimension`) VALUES ('1', '2D');
INSERT INTO `matrix_dimension` (`matrix_dimensionid`, `dimension`) VALUES ('2', '3D');
INSERT INTO `matrix_dimension` (`matrix_dimensionid`, `dimension`) VALUES ('3', '2.5D');

INSERT INTO `plate_format` (`plate_formatid`, `format`, `number_of_cols`, `number_of_rows`, `well_size`) VALUES ('1', '96', '12', '8', '8991.88');
INSERT INTO `plate_format` (`plate_formatid`, `format`, `number_of_cols`, `number_of_rows`, `well_size`) VALUES ('2', '48', '8', '6', '13440.5');
INSERT INTO `plate_format` (`plate_formatid`, `format`, `number_of_cols`, `number_of_rows`, `well_size`) VALUES ('3', '24', '6', '4', '19749.4');
INSERT INTO `plate_format` (`plate_formatid`, `format`, `number_of_cols`, `number_of_rows`, `well_size`) VALUES ('4', '12', '4', '3', '27545.4');
INSERT INTO `plate_format` (`plate_formatid`, `format`, `number_of_cols`, `number_of_rows`, `well_size`) VALUES ('5', '6', '3', '2', '40140.4');

INSERT INTO `instrument` (`instrumentid`,`conversion_factor`,`name`) VALUES ('1', '1.55038','generic microscope');

INSERT INTO `magnification` (`magnificationid`, `magnification_number`) VALUES ('1', '5x');
INSERT INTO `magnification` (`magnificationid`, `magnification_number`) VALUES ('2', '10x');
INSERT INTO `magnification` (`magnificationid`, `magnification_number`) VALUES ('3', '20x');
INSERT INTO `magnification` (`magnificationid`, `magnification_number`) VALUES ('4', '30x');

INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('1', 'ROCK', '1');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('2', 'IPA3', '1');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('3', 'IPA5', '1');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('4', 'Jasplakinolide', '1');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('8', 'Control', '2');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('9', 'Control + Drug Solvent', '2');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('10', 'EGF', '1');
INSERT INTO `treatment_type` (`treatment_typeid`, `name`, `treatment_category`) VALUES ('11', 'SP600125', '1');

INSERT INTO `treatment` (`treatmentid`, `drug_solvent`) VALUES ('1', 'DMSO');

INSERT INTO `user` (`userid`, `email`, `first_name`, `last_name`, `password`, `role`) VALUES ('1', 'root@root.com', 'root', 'root', '//gbtQOoNAjOz2xXIdrmhyiGBhSgATlw', 'ADMIN_USER');
INSERT INTO `user` (`userid`, `email`, `first_name`, `last_name`, `password`, `role`) VALUES ('2', 'standard@standard.com', 'standard', 'standard', 'mvkI/WGUBVIKM16csKCQ3wQS8hH4nHMs', 'STANDARD_USER');

INSERT INTO `ecm_density` (`ecm_densityid`, `ecm_density`) VALUES ('1', '1');
INSERT INTO `ecm_density` (`ecm_densityid`, `ecm_density`) VALUES ('2', '2');
INSERT INTO `ecm_density` (`ecm_densityid`, `ecm_density`) VALUES ('3', '4');

INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('1', 'Collagen I (bovine)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('2', 'Collagen I (human)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('3', 'Collagen I (rat tail)', '2');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('4', 'Laminin I (mouse)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('5', 'Laminin I (mouse)', '2');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('6', 'Vitronectin (human)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('7', 'BD Matrigel (mouse)', '2');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('8', 'Collagen IV (human)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('9', 'Collagen IV (mouse)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('10', 'Fibronectin (human)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('11', 'Fibronectin (bovine)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('12', 'Nutragen', '2');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('13', 'Collagen I (rat tail)', '1');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('14', 'Collagen I (rat tail)', '3');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('15', 'Laminin I (mouse)', '3');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('16', 'BD Matrigel (mouse)', '3');
INSERT INTO `ecm_composition` (`composition_typeid`, `composition_type`, `l_matrix_dimensionid`) VALUES ('17', 'Nutragen', '3');

INSERT INTO `ecm` (`ecmid`, `polymerisation_ph`, `l_composition_typeid`) VALUES ('1', 'phys.', '1');
INSERT INTO `ecm` (`ecmid`, `polymerisation_ph`, `l_composition_typeid`) VALUES ('2', 'high', '1');

INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('1', 'ORIS', '1');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('2', 'random seeding', '1');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('3', 'ORIS', '2');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('4', 'mixed', '2');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('5', 'MCTS', '2');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('6', 'ORIS', '3');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('7', 'random seeding', '3');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('8', 'scratch', '1');
INSERT INTO `assay` (`assayid`, `assay_type`, `l_matrix_dimensionid`) VALUES ('9', 'cell exclusion zone', '1');

