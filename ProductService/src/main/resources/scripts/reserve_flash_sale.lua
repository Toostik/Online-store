
local stock = tonumber(redis.call('GET', KEYS[1]))

if stock == nil then
    return -2
end

local quantity = tonumber(ARGV[1])

if stock < quantity then
    return -1
end

redis.call('DECRBY', KEYS[1], quantity)

return stock - quantity