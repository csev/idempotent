--- SAK-49540 --
UPDATE lti_tools SET pl_launch=1 WHERE
pl_coursenav = 1
AND
(pl_launch IS NULL OR pl_launch = 0) AND (pl_linkselection IS NULL OR pl_linkselection = 0);

UPDATE lti_tools SET pl_linkselection=1 WHERE
(pl_lessonsselection = 1 OR pl_contenteditor = 1 OR pl_assessmentselection = 1 OR pl_importitem = 1 OR pl_fileitem =1)
AND
(pl_launch IS NULL OR pl_launch = 0) AND (pl_linkselection IS NULL OR pl_linkselection = 0);

UPDATE lti_tools SET pl_launch=1 WHERE
(pl_launch IS NULL OR pl_launch = 0) AND (pl_linkselection IS NULL OR pl_linkselection = 0);
--- END SAK-49540 --
