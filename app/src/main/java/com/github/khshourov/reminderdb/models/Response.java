package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroResponse;

public record Response(AvroResponse payload, User user) {}
