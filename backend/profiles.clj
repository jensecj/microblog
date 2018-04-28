{:dev
 {:env
  {:database-url "localhost"
   :database-port 5432
   :api-port 8080}}
 :test
 {:env
  {:database-url ""
   :database-port 5432}}
 :prod
 {:env
  {:database-url ""
   :database-port 5432}}}
