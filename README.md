Idempotent
==========

Idempotent is a Sakai Servlet that allow us to build automated database
migrations that can be applied each time Sakai is compiled and started.

> Idempotence is the property of certain operations in mathematics and
computer science whereby they can be applied multiple times without
changing the result beyond the initial
application. - https://en.wikipedia.org/wiki/Idempotence

This works best for simple schema changes like adding a column to an
existing table.

See the [DESIGN.md](DESIGN.md) file for more information on the
approach used in this project.

Installation
------------

Idempotent is designed to be part of the Sakai distribution as a top-level
folder.  But for now it is like a contrib tool so you must manually install
it.

First you go into your Sakai source tree and check out this repo:

    cd ~/sakai/scripts/trunk

    git clone https://github.com/csev/idempotent.git
    cd idempotent

Until this is in the main repo, it does not ship with a `pom.xml`,
so you must copy one of the given versions of the `pom.xml` to
`pom.xml`:

    cp pom-24-SNAPSHOT.xml pom.xml
    cp servlet/pom-24-SNAPSHOT.xml servlet/pom.xml

or

    cp pom-23-SNAPSHOT.xml pom.xml
    cp servlet/pom-23-SNAPSHOT.xml servlet/pom.xml

Then just compile it:

    # if needed
    export MAVEN_OPTS=-Xms512m -Xmx1024m -Djava.util.Arrays.useLegacyMergeSort=true

    mvn -Dmaven.tomcat.home=/Users/csev/sakai/scripts/apache-tomcat-9.0.21 clean install sakai:deploy

It is installed in your Tomcat - it does not even need a Tomcat restart.  When the
servlet comes up - either because of a fresh deploy or when Sakai is started, it loads
and does its idempotent thing.

If it runs any SQL - that is displayed with a `log.info`.  The very first run, it will create
it table to track migrations:

    o.s.i.Util.ensureIdempotentTable Creating the SAKAI_IDEMPOTENT Table
    o.s.i.Util.runUpdateSql IDEMPOTENT-001(0): CREATE TABLE SAKAI_IDEMPOTENT ( MIGRATION_ID INT
        NOT NULL AUTO_INCREMENT, NOTE VARCHAR (256) NOT NULL, SQL_TEXT VARCHAR (1024) NOT NULL,
        CREATEDON DATETIME NULL,PRIMARY KEY (MIGRATION_ID) )

When it detects that a migration is needed it runs logs the SQL:

    o.s.i.Util.runMigrationOnce SAK-49633(1): UPDATE SAKAI_SITE SET TYPE='course'
        WHERE SITE_ID = '!plussite';

Once it has run the migrations, every time the webapp deploys and starts, it will check if
there are any migrations yet to run, and usually finding none it will have no log messages
at all.

From time to time, if this is not in the main repo, go into the `idempotent` folder
do a `git pull` and recompile using `mvn`.   You don't even need to restart Sakai.
With this approach your schema might time travel into the future a bit, but if
the SQL in idempotent is truly idempotent, that is OK.  It is better to have a column
that is unused before you get the code that will break without the column.


