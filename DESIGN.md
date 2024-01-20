Idempotent
==========

This is an extension to Sakai that will automatically do some of the simpler
database migrations / evolutions.   Sakai handles table creation / evolution as follows:

Current Database Migration Approach in Sakai
--------------------------------------------

When Sakai is started with an empty database there are a series of SQL
scripts that get run to do initial database setup.  These scripts are
in the source tree and have names like `sakai_site.sql` in `kernel`.

When Sakai is started with an already created database, if the `auto.ddl`
setting is set to `true`.  Various aspects of Sakai try to evolve their
database with limited success.  `auto.ddl` is a "best effort" and is not
sure to work so most production servers are run with `auto.ddl=false`
and limit database migration to moments where the version is
being upgraded. 

At each major and minor release of Sakai, great care is taken to provide
A conversion script associated with the upgrade.   These scripts are
made available in github:

https://github.com/sakaiproject/sakai-reference

These scripts are carefully developed by comparing the database schema
between two successive versions and generating necessary SQL statements
to evolve the schema.  Sometimes conversion scripts update live data
when the new version needs data in a different schema.

We produce both MySQL and Oracle versions of these "evolution" scripts.

When a Sakai server is upgraded across multiple version steps, the
database administrator is told to run the conversion scripts one at
a time in order to make sure all of the evolutions are done in order.

This approach has worked well for 20 years - but it does add a manual
step to every upgrade which makes it difficult to be somewhat
more automatic in upgrades to allow a server to grab a newer version
of a branch (like 23.x) without waiting for the next minor releases
and its conversion script.

Sakai Idempotent
----------------

If you look at the conversion script, there are generally two kinds
of entries - each marked with the Sakai JIRA it is associated with.
Many of the entries are quite simple.  Here are some examples from the
Sakai 23.0 to 23.1 conversion:

    -- SAK-48948
    ALTER TABLE PINNED_SITES ADD HAS_BEEN_UNPINNED BIT NOT NULL;
    -- SAK-48948 END

    -- SAK-49633
    -- Fix Site Type in !plussite template
    UPDATE SAKAI_SITE SET TYPE='course' WHERE SITE_ID = '!plussite';
    --- SAK-49633 END

The `SAK-48949` modification simply adds a column to an existing table.
If the `PINNED_SITES` table exists and the `HAS_BEEN_UNPINNED` column
is not in the table, it can be added any time.  If can be done before
or after any upgrade.  It is "idempotent" - you could even run the same
`ALTER` statement over and over - if the column was already there,
the ALTER would fail but not harm any data.

The `SAK-49633` is doing a little cleanup on some data that was put into
a table during an earliier data model migration incorrectly.  Now we need
to update it to "fix" that mistake.  This statement is "idempotent" because
it could run over and over and not blow up - but you really want to run it just
once to fix the "mistake".

The idea of Sakai Idempotent is to write some Java code that runs when Sakai
starts up and intelligently apply these "idempotent" changes making sure they
are applied properly.  

If a system admin was running Sakai 23.x and pulled changes, and they could also
get the latest 23.x Idempotent with the small and idempotent database
evolutions - when Sakai was compiled, deployed, and started, the migrations
would be applied a few seconds after start up.

And then later when the admin upgrades to the next 23.1 minor release, they still
will run the 23.0 -> 23.1 migration.  And some of the SQL commands will be run a
a second time - but since they are "idempotent", it is OK to run them twice.

Idempotent lets Sakai to get *closer* to a pure cloud deployment where you
can track a branch in production.

Not all migrations will fit into Idempotent - we will need ot gain experience
in this.  Over time, we might add features to Idempotent to the point where 
we can handle all or nearly all migrations through this approach.  but that will
take time and experience.  Initially we will keep expectations low
and limit Idempotent to doing "idempotent" evolutions.

