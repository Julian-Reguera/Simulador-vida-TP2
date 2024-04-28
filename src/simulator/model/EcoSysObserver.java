package simulator.model;

import java.util.List;

public interface EcoSysObserver {
	void onRegister(double time, MapInfo map, List<AnimalInfo> animals);

	void onReset(double time, MapInfo map, List<AnimalInfo> animals);

	void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a);

	void onRegionSet(int row, int col, MapInfo map, RegionInfo r);

	void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt);
}

/*
 * 
 * map: gestor de regiones animals: lista de animales a: un animal r: region
 * time: el tipo actual de la simulaicon dt: delta-time en el paso de simulación
 * correspondiente
 * 
 * SE USA MapInfo AnimalInfo y RegionInfo para evitar alteriar el estado de los
 * animales desde fuera
 * 
 */