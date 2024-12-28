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

Configuration
-------------

To enable Idempotent in your Sakai instance (once it is installed) you must
add the following to your `sakai.properties`:

    idempotent.enabled=true

Installation
------------

Idempotent is designed to be part of the Sakai distribution as a top-level
folder.  But for now it is in a separate repo like a contrib tool so you
must manually install it.

First you go into your Sakai source tree and check out this repo:

    cd ~/sakai/scripts/trunk

    git clone https://github.com/csev/idempotent.git
    cd idempotent

Until this is in the main repo, it does not ship with a `pom.xml`,
so you must copy one of the given versions of the `pom.xml` to
`pom.xml`:

    git checkout main
    cp pom-25-SNAPSHOT.xml pom.xml
    cp servlet/pom-25-SNAPSHOT.xml servlet/pom.xml

or

    git checkout 23.x
    cp pom-23-SNAPSHOT.xml pom.xml
    cp servlet/pom-23-SNAPSHOT.xml servlet/pom.xml

Then just compile it:

    mvn -Dmaven.tomcat.home=/Users/csev/sakai/scripts/apache-tomcat-9.0.21 clean install sakai:deploy

It is installed in your Tomcat - it does not even need a Tomcat restart.  When the
servlet comes up - either because of a fresh deploy or when Sakai is started, it loads
and does its idempotent thing.

If you are using the Dr. Chuck `sakai-scripts`, a partial compile is done with the `smv.sh`
command.

If it runs any SQL - that is displayed with a `log.info`.  The very first run, it will create
its table to track migrations:

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

Sakai's Conversion Scripts
--------------------------

In general, the current official source of these conversions is a set of versoin-based conversion scripts:

https://github.com/sakaiproject/sakai-reference/tree/master/docs/conversion

TODO: Work to be done
---------------------

Investigate: https://www.sitepoint.com/schema-migration-hibernate-flywaydb/

This should almost work with Oracle - the `SAKAI_IDEMPOTENT` table creation SQL needs
to be better and have a sequence.  And the `INSERT` statement in `Util.recordMigration()`
needs to be different for Oracle.

