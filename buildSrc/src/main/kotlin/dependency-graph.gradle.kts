import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension

plugins {
    id("com.vanniktech.dependency.graph.generator")
}

configure<DependencyGraphGeneratorExtension> {
    projectGenerators.create("VolleyballStats") {
        includeProject = { dependency ->
            !dependency.project.displayName.contains("test")
        }
    }
}
