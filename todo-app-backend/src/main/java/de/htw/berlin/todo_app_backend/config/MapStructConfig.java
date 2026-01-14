package de.htw.berlin.todo_app_backend.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MapStructConfig {
}
