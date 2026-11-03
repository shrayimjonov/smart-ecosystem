update product set type='Cigarette' where type='CigaretteBlock';
update product set type='Vodka' where type='VodkaBlock';
update product set type='Beer' where type='BeerBlock';
update product set type='NaturalWine' where type='NaturalWineBlock';
update product set type='Wine' where type='WineBlock';
update product set type='Liquor' where type='LiquorBlock';
update product set type='Cognac' where type='CognacBLock';



delete
from attributes_product_type
where product_types in (
                        'CigaretteBlock',
                        'VodkaBlock',
                        'BeerBlock',
                        'NaturalWineBlock',
                        'WineBlock',
                        'LiquorBlock',
                        'CognacBLock',
                        'WaterAndSoftDrinksBlock',
                        'WineMaterial',
                        'Ethereum',
                        'AlcoholTechnical',
                        'TinctureBlock',
                        'Alcohol',
                        'AlcoholTechnical',
                        'CognacAlcohol',
                        'ChampagneBlock',
                        'LiquorVodkaBlock'
    );