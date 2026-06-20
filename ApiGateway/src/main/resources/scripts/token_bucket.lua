local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local data = redis.call('HMGET', key, 'tokens', 'last_refill')

local tokens = tonumber(data[1])
local lastRefill = tonumber(data[2])

if tokens == nil then
    tokens = capacity
    lastRefill = now
end

local elapsed = now - lastRefill

local refill = math.floor(elapsed * refillRate)

tokens = math.min(capacity, tokens + refill)

if tokens <= 0 then
    redis.call(
        'HMSET',
        key,
        'tokens',
        tokens,
        'last_refill',
        now
    )

    redis.call('EXPIRE', key, 86400)

    return -1
end

tokens = tokens - 1

redis.call(
    'HMSET',
    key,
    'tokens',
    tokens,
    'last_refill',
    now
)

redis.call('EXPIRE', key, 86400)

return tokens