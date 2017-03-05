(ns mishmash.http-handlers
  (:require [mishmash.event-logger :as event-logger]))

(defonce base-event (atom {}))

(defn wrap-log-request-duration
  [handler]
  (fn [req]
    (let [response (atom nil)
          error (atom nil)
          start-time (System/nanoTime)
          ]
      (try
        (reset! response (handler req))
        (catch Throwable e
          (reset! error e)))
      (let [duration (/ (- (System/nanoTime) start-time) 1e6)]
        (event-logger/with-merged-event @base-event
          (if @error
            (event-logger/log {:service "http.duration" :state "critial" :metric duration})
            (event-logger/log {:service "http.duration" :state "ok" :metric duration})
            ))
        (if @error
          (throw @error)
          @response)))))
