# sql-match
Able to modify and match two MySQL databases and generate modified SQL, suitable for dev->test/uat->prod database matching, no longer need to record each SQL


## how to use
com.xin.sql.Main is boot file, you can change the properties of connMsaA and connMsgB, then execute main method.
otherSetting is optional setting

support polarDb


``` java

public static void main(String[] args) throws SQLException {

        // demo
        ConnMsg connMsgA = new ConnMsg("localhost",3306,"flink_test","xin","");
        ConnMsg connMsgB = new ConnMsg("localhost",3306,"flink_test2","xin","");
        OtherSetting otherSetting = new OtherSetting(true,true,"");

        MySql fromDatabase = new MySql(connMsgA);
        MySql toDatabase = new MySql(connMsgB);


        MatchSql matchSql = new MatchSql(otherSetting, fromDatabase, toDatabase);
        List<String> allModifySql = matchSql.match();

        System.out.println();
        for (String sql : allModifySql) {

            System.out.println(sql);
            System.out.println();
            System.out.println();
        }

        System.exit(0);
    }

```
print result:

``` 
DROP TABLE transactions ;


CREATE TABLE `student` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `sex` varchar(5) NOT NULL,
  PRIMARY KEY (`id`)
) ;



ALTER TABLE `spend_report`
DROP COLUMN `test`, 
DROP COLUMN `amount_ex`, 
ADD COLUMN amount bigint NULL AFTER `log_ts`, 
MODIFY COLUMN account_id bigint NOT NULL FIRST,  # Type mismatch, Original varchar(100) : Current bigint; 
DROP INDEX `idx`;

``` 
