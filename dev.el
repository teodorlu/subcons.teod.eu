;;; exec.el --- Description -*- lexical-binding: t; -*-
;;
;;; Commentary:
;;
;;  Shortand for starting up a few vterms with compilation processes
;;
;;; Code:

(defun subcons/dev ()
  "Is this doc?"
  (async-shell-command "bin/live-server" "*subcons/live-server*")
  (async-shell-command "bin/watch" "*subcons/watch*"))

(subcons/dev)

(provide 'exec)
;;; exec.el ends here
