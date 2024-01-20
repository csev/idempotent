Idempotent
==========

Idempotent is a Sakai Servlet that allow us to build automated database
migrations that can be applied each time Sakai is compiled and started.

This works best for simple schema chancge like adding a column to an
existing table.  

See the DESIGN.md file for more information oin the approach used in this
project.

Installation
------------

Idempotent is designed to be part of the Sakai distribution as a top-level
folder.  But for now it is like a contrib tool so you must manually install
it.

First you go into your Sakai source tree and check out this repo:

    cd ~/sakai/scripts/trunk

    https://github.com/csev/idempotent.git



Under construction

This is done using a servlet so that it can be reloaded dynamically.

