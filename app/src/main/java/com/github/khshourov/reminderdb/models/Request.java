package com.github.khshourov.reminderdb.models;

import com.github.khshourov.reminderdb.avro.AvroRequest;

public record Request(AvroRequest avroRequest, User user) {}
