;;; exec.el --- Description -*- lexical-binding: t; -*-
;;
;;; Commentary:
;;
;;  Shortand for starting up a few vterms with compilation processes
;;
;;; Code:

(defun subcons/dev ()
  "Is this doc?"
  (async-shell-command "bb serve" "*subcons/live-server*"))

;; (subcons/dev)

(provide 'exec)
;;; exec.el ends here
