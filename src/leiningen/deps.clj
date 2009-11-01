(ns leiningen.deps
  (:require [lancet])
  (:use [leiningen]
        [clojure.contrib.java-utils :only [file]])
  (:import [org.apache.maven.model Dependency]
           [org.apache.maven.artifact.ant DependenciesTask]
           [org.apache.tools.ant.util FlatFileNameMapper]))

(defn- make-dependency [[group name version]]
  (doto (Dependency.)
    (.setGroupId group)
    (.setArtifactId name)
    (.setVersion version)))

(defn deps
  "Install dependencies in lib/"
  [project & args]
  (let [deps-task (DependenciesTask.)]
    (.setBasedir lancet/ant-project (:root project))
    (.setFilesetId deps-task "dependency.fileset")
    (.setProject deps-task lancet/ant-project)
    (.setPathId deps-task (:name project))
    (doseq [dep (:dependencies project)]
      (.addDependency deps-task (make-dependency dep)))
    (.execute deps-task)
    (.mkdirs (file (:root project) "lib"))
    (lancet/copy {:todir "lib/"}
                 (.getReference lancet/ant-project
                                (.getFilesetId deps-task))
                 (FlatFileNameMapper.))))