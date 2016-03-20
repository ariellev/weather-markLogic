cols <- append(cols, c(32:33, 2:3, 7))

d3 <- filter(d, LATITUDE > 0)
d3 <- filter(d, LONGITUDE > 0)

library(jsonlite)

n <- c("event_type", "fatalities", "injuries", "prop_dmg", "prop_dmg_exp", "crop_dmg", "crop_dmg_exp", "remarks", "latitude", "longitude", "date", "time", "state")
names(d4) <- n
dates <- as.Date(d4$date,  "%m/%d/%Y %H:%M:%S")

id <- paste(d4$event_type, d4$latitude, d4$longitude, d4$date, d4$time, sep = "_")
dig <- sapply(id, function(x) { digest(x, algo="sha512")})
dig1 <- paste(1:length(dig), dig, sep="_")
d4$id <- dig1

x <- toJSON(d4, pretty = T)
write(x, "data.json")