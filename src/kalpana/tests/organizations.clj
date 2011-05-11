(ns kalpana.tests.organizations
  (:use [error.handler :only [with-handlers handle ignore]]
        [com.redhat.qe.verify :only [verify]]
        [test-clj.testng :only [gen-class-testng data-driven]])
  (:require [kalpana.tasks :as tasks]
            [kalpana.validation :as validate])
  (:import [org.testng.annotations Test BeforeClass]))

(defn ^{Test {:groups ["organizations"]}} create_simple [_]
  (tasks/verify-success #(tasks/create-organization (tasks/timestamp "auto-org") "org description")))

(defn ^{Test {:groups ["organizations"]}} delete_simple [_]
  (let [org-name (tasks/timestamp "auto-del")]
    (tasks/create-organization org-name "org to delete immediately")
    (tasks/delete-organization org-name)))

(defn ^{Test {:groups ["organizations" "validation" ]}} duplicate_disallowed [_]
  (let [org-name (tasks/timestamp "test-dup")]
    (validate/duplicate_disallowed
     #(tasks/create-organization org-name "org-description"))))

(defn ^{Test {:groups ["organizations" "validation"]}} name_required [_]
  (validate/name-field-required #(tasks/create-organization nil "org description")))

(defn valid_name [name expected-error]
  (validate/field-validation #(tasks/create-organization name "org description") expected-error))

(data-driven valid_name {Test {:groups ["organizations" "validation"]}}
             (vec (concat 
                   (validate/variations [:invalid-character :name-must-not-contain-characters])
                   (validate/variations [:trailing-whitespace :name-must-not-contain-trailing-whitespace]))))

(gen-class-testng)

